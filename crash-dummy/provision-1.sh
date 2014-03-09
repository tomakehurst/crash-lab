yum install -y java-1.7.0-openjdk.x86_64 lsof wget nc

# Set up and run WireMock
[[ -z $(grep "wiremock" /etc/passwd) ]] && useradd wiremock
rm -rf /usr/lib/wiremock
cp -r /mnt/crash-test/wiremock /usr/lib
cd /usr/lib/wiremock
[[ -f wiremock-1.44-standalone.jar ]] || wget http://central.maven.org/maven2/com/github/tomakehurst/wiremock/1.44/wiremock-1.44-standalone.jar
[[ -z $(pgrep -u wiremock java) ]] && nohup su -c "java -jar wiremock-1.44-standalone.jar --verbose" wiremock &
chown -R wiremock:wiremock /usr/lib/wiremock

# Set up and run Saboteur
rpm -ivh https://github.com/tomakehurst/saboteur/releases/download/v0.6/saboteur-0.6-1.noarch.rpm
service saboteur-agent start