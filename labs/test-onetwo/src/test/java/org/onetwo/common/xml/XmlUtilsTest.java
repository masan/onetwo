package org.onetwo.common.xml;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.onetwo.common.utils.CUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.xml.XmlUtils;

public class XmlUtilsTest {
	
	static class TestPerson {
		private String userName;
		private int age;
		private TestPerson parent;
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public TestPerson getParent() {
			return parent;
		}
		public void setParent(TestPerson parent) {
			this.parent = parent;
		}
		
	}
	
	@Test
	public void testPerson(){
		TestPerson hh = new TestPerson();
		hh.setUserName("hanhan");
		hh.setAge(30);
		
		TestPerson parent = new TestPerson();
		parent.setUserName("hanrenjun");
		parent.setAge(60);
		
		hh.setParent(parent);
		
		String xmlStr = XmlUtils.toXML(hh, "person", TestPerson.class).trim();
		
		System.out.println("testPerson xml:\n " + xmlStr);
		
		TestPerson newHH = XmlUtils.toBean(xmlStr, "person", TestPerson.class);
		Assert.assertEquals(hh.getUserName(), newHH.getUserName());
	}
	
	@Test
	public void testPersonList(){
		TestPerson hh = new TestPerson();
		hh.setUserName("hanhan");
		hh.setAge(30);
		
		TestPerson parent = new TestPerson();
		parent.setUserName("hanrenjun");
		parent.setAge(60);
		
		hh.setParent(parent);
		
		Collection<TestPerson> lists = CUtils.newHashSet();
		lists.add(hh);
		
		String xmlStr = XmlUtils.toXML(lists);
		
		System.out.println("testPersonList xml:\n " + xmlStr);
		
		Collection<TestPerson> list = XmlUtils.toBean(xmlStr, "person", List.class);
		Assert.assertEquals(hh.getUserName(), list.iterator().next().getUserName());
	}

}