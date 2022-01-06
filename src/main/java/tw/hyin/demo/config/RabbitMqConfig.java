/**
 * 
 */
package tw.hyin.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import tw.hyin.demo.pojo.MessageObj;
import tw.hyin.demo.utils.Log;

/**
 * @author YingHan 2022-01-04
 *
 */
@Component
public class RabbitMqConfig {

	public static final String TOPIC_EXCHANGE_NAME = "demo-exchange";

	public static final String SERVER_QUEUE = "server-queue";

	public static final String ROUTING_KEY = "demo-routing-key";

	@Bean
	public Queue server_queue() {
		// 隊列名稱
		return new Queue(SERVER_QUEUE, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(TOPIC_EXCHANGE_NAME);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
	}

	@Bean
	public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
		//將自定義的消息類序列化成 json 格式
		return new Jackson2JsonMessageConverter(objectMapper);
	}

	@RabbitListener(queues = SERVER_QUEUE)
	public void receiveMessage(MessageObj message) {
		// 改用@RabbitListener指定接收訊息的方法 (監聽 client 傳來的訊息)
		// 不需要MessageListenerContainer及MessageListener的相關設定
		System.out.println("Received message:" + message.toString());
		Log.info("Received message:{}", message.toString());
	}

}
