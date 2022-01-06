/**
 * 
 */
package tw.hyin.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import tw.hyin.demo.pojo.MessageObj;
import tw.hyin.demo.pojo.ResponseObj;
import tw.hyin.demo.pojo.ResponseObj.RspMsg;
import tw.hyin.demo.service.MessageService;
import tw.hyin.demo.utils.Log;

/**
 * @author YingHan 2022-01-04
 *
 */
@RestController
@ResponseBody
@RequestMapping("/mq")
public class MsgController extends BaseController {

	private final MessageService messageService;

	@Autowired
	public MsgController(MessageService messageService) {
		this.messageService = messageService;
	}

	@ApiOperation(value = "message broker 上傳測試")
	@PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObj<RspMsg>> send(HttpServletRequest request, @RequestBody MessageObj msg) {
		try {
			//取得傳送訊息的IP位址
			msg.setIpAddress(super.getClientIpAddr(request));
			messageService.send(msg);
			System.out.println("Sending message:" + msg.toString());
			Log.info("Sending message:{}", msg.toString());
			return super.sendSuccessRsp(RspMsg.SUCCESS);
		} catch (Exception e) {
			return super.sendFailRsp(e);
		}
	}

}
