package tw.hyin.demo.service;

import tw.hyin.demo.pojo.MessageObj;

/**
 * @author YingHan 2022-01-04
 */
public interface MessageService {

	/**
	 * @author YingHan
	 * @since 2022-01-04
	 * 
	 * @Description 傳送訊息至 queue
	 */
	public void send(MessageObj message) throws Exception;

}
