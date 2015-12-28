1, config.hostpath=http://210.202.111.104
修改為 config.hostpath=http://ckapi.51vapp.com

2,註解context.xml
<Resource name="vstoreck/apds"
            auth="Container"
            type="javax.sql.DataSource"
            username="vstore"
            password="vstore502"
            driverClassName="com.mysql.jdbc.Driver"
            url="jdbc:mysql://10.8.10.36:3306/vstore?characterEncoding=UTF-8"
            maxActive="100"
            maxIdle="20"
            maxWait="5000"
            validationQuery="select 1"
            />
