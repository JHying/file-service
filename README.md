# Introduction

連接至 RabbitMQ，包含檔案上傳、下載、新增浮水印、報表製作功能

1. 開發環境：Eclipse

2. 專案建置：Maven

3. 語言版本：JDK 8

4. 執行：jar

5. 框架：spring boot 2.5.5

6. ORM：Spring Data JPA 2.5.5 (包含 hibernate 5.4.32.Final)

7. Log：log4j2 2.5.5（Util 位於 **/utils/Log）
		
### Detail

1. 設定檔位置：src.main.resources

2. 使用 maven build

3. 包含兩種環境配置：dev & prod

4. 可依據 maven build 參數決定要 build 的檔案（dev 配置檔放在 dev 資料夾，prod 配置檔放在 prod 資料夾）

5. log4j2.xml：log 配置檔－－分別產生 INFO 及 ERROR 資訊的 Log 檔

# 測試範例

RabbitMQ 設定於：RabbitMqConfig

	/upload -- 檔案上傳
	
	/download/[file_path] -- 檔案下載
	
	/download/watermark/[file_path] -- 檔案新增浮水印後下載（浮水印包在 header name=watermark）
	
	/report/[templateName] -- 報表製作（測試模板的 templateName=test）
	
	/mq/send -- RabbitMQ 上傳測試（可從 Log INFO 紀錄查看傳送訊息)