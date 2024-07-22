# Websocket practice 

1. Initialize a new project
   使用 spring-cli 詠唱咒語
   ```bash
   $ spring init --name=websocket-practice -dependencies=websocket websocket-practice
   Using service at https://start.spring.io
   Project extracted to '/<path-to-your-dir>/websocket-practice'
   ```

2. 新增一個 `config` package 底下的 `WebSocketConfig.java`
   - `@EnableWebSocketMessageBroker`: 掛在類別上，用來啟用 WebSocker Server
   - 實作 `WebSockerMessageBrokerConfigurer` 介面：
     - `registerStompEntpoints(registry)`: 註冊一個 websocket endpoint
       - 目的：client 會用這個 endpoint 與 websocket server 建立連線
       - `withSockJS()` 用來啟用 fallback options 給不支援 websocket 的 browser
       - `STOMP`: 全名 simple text oriented messaging protocol，定義資料交換的格式與規則
     - `configureMessageBroker(registry)`: 用來 route 訊息從 clientA 到 clientB
       - message destination 以 `/topic` 作前綴字的，要路由到 message broker，這個 message broker 會把訊息廣播到所有訂閱特定主題的 clients
       - message destination 開頭是 `/app` 前綴的，應該要路由到 messaging-handling 方法（後續定義）
     - 以上範例使用 in-memory message broker，也可以改成其他功能更完整的 message broker，例如 RabbitMQ 或者 ActiveMQ


       