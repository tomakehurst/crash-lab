yum install -y java-1.7.0-openjdk.x86_64 lsof wget nc ruby rubygems ruby-devel rpm-build

gem install fpm 

# Set up and run WireMock
[[ -z $(grep "wiremock" /etc/passwd) ]] && useradd wiremock
[[ -d /usr/lib/wiremock ]] || mkdir -p /usr/lib/wiremock
chown -R wiremock:wiremock /usr/lib/wiremock
cd /usr/lib/wiremock
[[ -f wiremock-1.37-standalone.jar ]] || wget http://central.maven.org/maven2/com/github/tomakehurst/wiremock/1.37/wiremock-1.37-standalone.jar
[[ -z $(pgrep -u wiremock java) ]] && nohup su -c "java -jar wiremock-1.37-standalone.jar --verbose" wiremock &

