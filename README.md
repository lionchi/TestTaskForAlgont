Программа работает по адресу localhost:8080.
У меня rabbitmq был развернут в докере (docker-compose.yml) и также пришлось подключать плагин rabbitmq-plugins enable rabbitmq_stomp. Параметры подключение к rabbitmq прописаны в application.yml
Для того чтобы получать сообщения о загруженности CPU, необходимо нажать на кнопку Connected. Новые сообщения будут появляться раз в 3 секунды. Масимальное значение загруженности определяется в application.yml max.value.cpu
