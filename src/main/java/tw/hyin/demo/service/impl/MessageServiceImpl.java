/**
 * 
 */
package tw.hyin.demo.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.hyin.demo.config.RabbitMqConfig;
import tw.hyin.demo.dao.MessageRecordDao;
import tw.hyin.demo.entity.MessageRecord;
import tw.hyin.demo.pojo.MessageObj;
import tw.hyin.demo.service.MessageService;

/**
 * @author YingHan 2022-01-04
 *
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

	private final RabbitTemplate rabbitTemplate;
	private final MessageRecordDao messageRecordDao;

	@Autowired
	public MessageServiceImpl(RabbitTemplate rabbitTemplate, MessageRecordDao messageRecordDao) {
		this.rabbitTemplate = rabbitTemplate;
		this.messageRecordDao = messageRecordDao;
	}

	@Override
	public void send(MessageObj message) throws Exception {
		// 傳送至quene
		rabbitTemplate.convertAndSend(
				RabbitMqConfig.TOPIC_EXCHANGE_NAME,
				RabbitMqConfig.ROUTING_KEY,
				message);
		// 寫入傳送紀錄
		this.saveSendRecord(message);
	}

	/**
	 * ---------------------------------- PRIVATE ----------------------------------
	 **/

	/**
	 * @author YingHan
	 * @since 2022-01-04
	 * 
	 * @Description 儲存訊息傳送紀錄
	 */
	private void saveSendRecord(MessageObj message) throws Exception {
		messageRecordDao.saveBean(MessageRecord.builder()
				.ipAddress(message.getIpAddress())
				.message(message.getJsonMsg())
				.updateDate(message.getUpdateDate()).build());
	}

}
