package org.onetwo.ext.ons.consumer;

import org.onetwo.ext.alimq.ConsumContext;
import org.onetwo.ext.ons.annotation.ONSConsumer;
import org.onetwo.ext.ons.annotation.ONSSubscribe;
import org.onetwo.ext.ons.producer.ONSProducerTest;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;


/**
 * @author wayshall
 * <br/>
 */
@ONSConsumer
public class AnnotaionConsumer  {

	@ONSSubscribe(consumerId="CID_ONETWO_TEST2", topic=ONSProducerTest.TOPIC, subExpression="order-pay", consumeFromWhere=ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET)
	public void doConsume(ConsumContext consumContext) {
		System.out.println("注解消费者，收到消息：" + consumContext.getDeserializedBody());
	}

}
