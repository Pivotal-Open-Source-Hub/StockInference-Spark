# The Vagrant VM

This VM has all the required software to run the lab, including:

* Apache Geode (Incubating)
* Spring XD 2.0 snapshot
* Apache Spark 1.3.1
* Apache Zeppelin (Incubating)
* R
* Gradle 2.3
* Maven 3

## Requirements

* Vagrant
* VirtualBox

# Instructions

* Starting the VM
```
$ vagrant up
```

* Using SSH
```
$ vagrant ssh
[vagrant@stocks-vm, load: 0.01]
~ $
```
* Run the test script

```
$ ?
```
* Stopping the VM

```
$ vagrant halt
```

# Preparing the VM from scratch

*Based on Ubuntu 15.04 Vivid*

* Dependencies

```
sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install -y oracle-java8-installer
sudo apt-get install -y libcurl4-gnutls-dev nodejs maven npm unzip docker.io r-base-dev r-base
sudo apt-get -f install
wget http://www.scala-lang.org/files/archive/scala-2.10.5.deb
sudo dpkg -i scala-2.10.5.deb
sudo usermod -aG docker vagrant
sudo service ntp stop
sudo ntpd -gq
sudo service ntp start
```
* Cloning and building projects

```
# Geode
git clone github.com/apache/incubator-geode
git clone http://github.com/apache/incubator-geode
cd incubator-geode/
git checkout develop
./gradlew build -Dskip.tests=true
cd gemfire-spark-connector/
sbt package publishM2 publishLocal
```

```
# Zeppelin
git clone https://github.com/apache/incubator-zeppelin
cd incubator-zeppelin/
mvn clean package -Pspark-1.3 -Dhadoop.version=2.2.0 -Phadoop-2.2 -DskipTests
```

```
# SpringXD
wget http://repo.springsource.org/libs-snapshot-local/org/springframework/xd/spring-xd/2.0.0.BUILD-SNAPSHOT/spring-xd-2.0.0.BUILD-20150807.070129-71-dist.zip
unzip spring-xd-2.0.0.BUILD-20150807.070129-71-dist.zip
```

```
# Spark
wget http://www.interior-dsgn.com/apache/spark/spark-1.3.1/spark-1.3.1-bin-hadoop2.6.tgz
tar -xzvpf spark-1.3.1-bin-hadoop2.6.tgz
rm -rf spark-1.3.1-bin-hadoop2.6.tgz
```

* Edit the `.bashrc` and `$PATH`

Add the following lines at the end of `~/.bashrc`

```
export GEODE_HOME=/home/vagrant/incubator-geode/gemfire-assembly/build/install/apache-geode
export SPRINGXD_HOME=~/spring-xd-2.0.0.BUILD-SNAPSHOT
export ZEPPELIN_HOME=~/incubator-zeppelin
export PROJECT=/home/vagrant/StockInference-Spark
export SPARK_HOME=/home/vagrant/spark-1.3.1-bin-hadoop2.6

export PATH=$PATH:$GEODE_HOME/bin:$SPRINGXD_HOME/xd/bin:$SPRINGXD_HOME/shell/bin:$ZEPPELIN_HOME/bin:$SPARK_HOME/bin
```

* Pulling Docker image for Geode

```
sudo /etc/init.d/docker start
sudo docker pull apachegeode/geode:unstable
```
