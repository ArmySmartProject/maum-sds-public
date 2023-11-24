#!/usr/bin/env bash
SDS_IP="182.162.19.19"
DB_IP="10.122.64.116"
DB_PORT="1433"
CONNECTORS_IP="182.162.19.19"
CONNECTORS_PORT="50000"
CSS_ROUTE="\/home\/minds\/brain_sds\/imgUpload"

sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-analysis/src/main/resources/mybatis/db.properties
sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-log-store/src/main/resources/mybatis/db.properties
sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-maker/src/main/resources/mybatis/db.properties
sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-collector/src/main/resources/mybatis/db.properties
sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-frontend/src/main/resources/mybatis/db.properties

sed -i 's/fast\-aicc\.maum\.ai\:50000/'$CONNECTORS_IP'\:'$CONNECTORS_PORT'/g' brain-sds-frontend/src/main/resources/application.properties
sed -i 's/\/home\/ubuntu\/imgUpload/'$CSS_ROUTE'/g' brain-sds-frontend/src/main/resources/application.properties
sed -i 's/\/home\/ubuntu\/imgUpload/'$CSS_ROUTE'/g' brain-sds-builder/src/main/resources/config/spring/context-values.xml
sed -i 's/https\:\/\/sds.maum.ai\:443\/imagesLi\//https\:\/\/'$SDS_IP'\/imagesLi\//g' brain-sds-builder/src/main/resources/config/spring/context-values.xml
sed -i 's/182\.162\.19\.19\:1433/'$DB_IP'\:'$DB_PORT'/g' brain-sds-builder/src/main/resources/config/spring/context-datasource.xml

sed -i 's/182\.162\.19\.19/'$SDS_IP'/g' brain-sds-concierge/app.js
sed -i 's/182\.162\.19\.19/'$SDS_IP'/g' brain-sds-builder/src/main/webapp/WEB-INF/web.xml

sed -i 's/sds\.maum\.ai/'$SDS_IP'/g' brain-sds-builder/src/main/webapp/js/builderContents/chatbotBuilderSetting.js
sed -i 's/sds\.maum\.ai/'$SDS_IP'/g' brain-sds-builder/src/main/webapp/js/builderContents/chatbotBuilderAnswer.js

sed -i 's/sds\.maum\.ai/'$SDS_IP'/g' brain-sds-test-page/static/resources/js/chatbot.js
sed -i 's/sds\.maum\.ai/'$SDS_IP'/g' brain-sds-test-page/templates/host_dev.html


rm -r sds_artifact
mvn clean
mvn install
mkdir sds_artifact
mkdir sds_artifact/jar

cp brain-sds-adapter/target/brain-sds.jar sds_artifact/jar/brain-sds-adapter.jar
cp brain-sds-analysis/target/analysis-0.0.1-SNAPSHOT.jar sds_artifact/jar/brain-sds-analysis.jar
cp brain-sds-collector/target/brain-sds.jar sds_artifact/jar/brain-sds-collector.jar
cp brain-sds-data/target/brain-sds-data.jar sds_artifact/jar/brain-sds-data.jar
cp brain-sds-frontend/target/brain-sds.jar sds_artifact/jar/brain-sds-frontend.jar
cp brain-sds-log-store/target/brain-sds.jar sds_artifact/jar/brain-sds-log-store.jar
cp brain-sds-maker/target/brain-sds.jar sds_artifact/jar/brain-sds-maker.jar
cp brain-sds-util/target/brain-sds.jar sds_artifact/jar/brain-sds-util.jar
cp -r brain-sds-memory/ sds_artifact/
cp -r brain-sds-test-page/ sds_artifact/
