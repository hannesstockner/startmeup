# Pull base image.
FROM ubuntu:14.04

RUN DEBIAN_FRONTEND=noninteractive apt-get -y install software-properties-common
RUN apt-get -y install wget zip

# Install Java.
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer


# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Gradle
WORKDIR /usr/bin
RUN wget https://services.gradle.org/distributions/gradle-2.4-bin.zip && \
  unzip gradle-2.4-bin.zip && \
  ln -s gradle-2.4 gradle && \
  rm gradle-2.4-bin.zip

# Set Environmental Variables
ENV GRADLE_HOME /usr/bin/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

ADD . /srv
WORKDIR /srv

RUN gradle clean test fatJar

CMD java -jar /srv/build/libs/startmeup-all-1.0.jar

