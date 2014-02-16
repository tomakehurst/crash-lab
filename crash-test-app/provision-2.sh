yum install -y lsof wget nc 

echo "export JAVA_HOME=/usr/java/jdk1.7.0_51" > /etc/profile.d/java.sh
chmod +x /etc/profile.d/java.sh

chmod a+rwx /var/log

mkdir -p /var/log/crash-test
chmod a+rws /var/log/crash-test
