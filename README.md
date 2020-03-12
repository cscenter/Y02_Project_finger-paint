# FingerPaint

## Запуск сервера
1. В соответствии с [документацией](https://developers.google.com/identity/sign-in/android/start-integrating) зарегистрировать приложение и получить SERVER_CLIENT_ID, после чего указать его в ресурсах проекта app в network.xml
2. Тот же самый SERVER_CLIENT_ID нужно указать в проекте server в файле AuthServer.kt
3. Указать url сервера в ресурсах проекта app в network.xml
4. В в проекте server в файле AuthServer.kt указать пароль для доступа администратора - будут доступны запросы на добавление картинок и новых заданий
5. Создать базу данных на сервере:
    1. установить утилиту `psql`
    2. создать пользователя root `createuser -sW root` и указать пароль root123
    3. создать базу данных `createdb -O root fingerpaint`
    4. подключиться к БД `psql -d fingerpaint -U root -W -h localhost` и выполнить скрипт из файла schema.sql в проекте server
    Логин, пароль и название БД можно поменять в проекте server в файле persistence.xml
6. Запустить сервер `./gradlew :server:run`