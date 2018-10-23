#
# Download dependencies from govway.org and build govway
#
FROM java:latest
ENV GOVWAY_VERSION 3.0.0
ENV ANT_VERSION 1.10.5

RUN mkdir /govway
WORKDIR /govway
ADD . /govway
# RUN wget --no-clobber http://www.govway.org/download/govway-installer-${GOVWAY_VERSION}.tgz

# Eventually download and extract libs
RUN wget --no-clobber http://www.govway.org/download/govway-external-lib-${GOVWAY_VERSION}.tgz \
	&& tar -C lib/ -xf govway-external-lib-${GOVWAY_VERSION}.tgz govway-external-lib-${GOVWAY_VERSION}/lib/ --strip-components=2

# Eventually download and extract ant
RUN wget --no-clobber http://apache.panu.it//ant/binaries/apache-ant-${ANT_VERSION}-bin.zip \
	&& unzip -d /opt/ apache-ant-${ANT_VERSION}-bin.zip

# build
RUN /opt/apache-ant-${ANT_VERSION}/bin/ant
