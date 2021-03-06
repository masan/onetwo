package org.onetwo.common.project;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.onetwo.common.file.FileUtils;
import org.onetwo.common.log.JFishLoggerFactory;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author wayshall
 * <br/>
 */
public class ProjectRefactor {
	final private Logger logger = JFishLoggerFactory.getLogger(this.getClass());
	
	final private String baseDir;
	private String charset = FileUtils.DEFAULT_CHARSET;
	private List<BaseFileRefactor<?>> fileRefactors = Lists.newArrayList();

	public ProjectRefactor(String baseDir) {
		super();
		this.baseDir = baseDir;
	}
	
	public ProjectRefactor charset(String charset) {
		this.charset = charset;
		return this;
	}
	
	public FileReplacementsRefactor newFileReplacement(){
		FileReplacementsRefactor r = new FileReplacementsRefactor(baseDir);
		r.charset(charset);
		fileRefactors.add(r);
		return r;
	}
	public FileDeleteRefactor newFileDelete(){
		FileDeleteRefactor r = new FileDeleteRefactor(baseDir);
		fileRefactors.add(r);
		return r;
	}
	
	public void execute(){
		fileRefactors.forEach(r->{
			r.process();
		});
	}
	
	abstract class BaseFileRefactor<R extends BaseFileRefactor<R>> {
		final protected String baseDir;
		protected Predicate<File> fileMatcher;
		
		public BaseFileRefactor(String baseDir) {
			super();
			this.baseDir = baseDir;
		}

		@SuppressWarnings("unchecked")
		public R fileMatcher(Predicate<File> fileMatcher){
			this.fileMatcher = fileMatcher;
			return (R)this;
		}
		
		public R orFileMatcher(Predicate<File> matcher){
			return fileMatcher(this.fileMatcher==null?matcher:this.fileMatcher.or(matcher));
		}
		
		public R andFileMatcher(Predicate<File> matcher){
			return fileMatcher(this.fileMatcher==null?matcher:this.fileMatcher.and(matcher));
		}

		public void process(){
			File dirFile = new File(baseDir);
			FileUtils.list(dirFile, fileMatcher, true)
					 .forEach(file->{
						 doRefactor(file);
					 });
		}
		
		abstract protected void doRefactor(File file);
		
		public ProjectRefactor end(){
			return ProjectRefactor.this;
		}
	}
	
	public class FileReplacementsRefactor extends BaseFileRefactor<FileReplacementsRefactor> {

		private Map<String, String> textReplacements = Maps.newLinkedHashMap();
		private String charset = FileUtils.DEFAULT_CHARSET;
		
		public FileReplacementsRefactor(String baseDir) {
			super(baseDir);
		}
		public FileReplacementsRefactor charset(String charset) {
			this.charset = charset;
			return this;
		}
		
		public FileReplacementsRefactor textReplace(String source, String target){
			textReplacements.put(source, target);
			return this;
		}

		@Override
		protected void doRefactor(File file) {
			if(file.isDirectory()){
				return ;
			}
			logger.info("doTextReplacement for : " + file.getPath());
			String text = FileUtils.readAsString(file, charset);
			for(Entry<String, String> entry : this.textReplacements.entrySet()){
				text = text.replace(entry.getKey(), entry.getValue());
			}
			FileUtils.writeStringToFile(file, charset, text);
		}
		
	}
	

	public class FileDeleteRefactor extends BaseFileRefactor<FileDeleteRefactor> {
		
		public FileDeleteRefactor(String baseDir) {
			super(baseDir);
		}
		
		public FileDeleteRefactor fileNameEndWith(String...postfix){
			return orFileMatcher(file->{
				return Stream.of(postfix).anyMatch(suffix->{
					return file.getName().endsWith(suffix);
				});
			});
		}
		
		public FileDeleteRefactor dirNameEqual(String...dirNames){
			return orFileMatcher(file->{
				return Stream.of(dirNames).anyMatch(dirName->{
					boolean res = file.isDirectory() && file.getName().equals(dirName);
//					logger.info("file[{}] match dir res: {}", file.getPath(), res);
					return res;
				});
			});
		}
		
		public FileDeleteRefactor fileNameEqual(String...fileNames){
			return orFileMatcher(file->{
				return Stream.of(fileNames).anyMatch(fileName->{
					return file.isFile() && file.getName().equals(fileName);
				});
			});
		}

		@Override
		protected void doRefactor(File file) {
			if(FileUtils.delete(file, true)){
				logger.info("delete succeed: {}", file.getPath());
			}else{
				logger.info("delete failed : {}", file.getPath());
			}
		}
		
	}

}
