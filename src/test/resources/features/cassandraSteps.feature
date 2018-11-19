Feature: Cassandra steps test

  Scenario: Connect to Cassandra
    Given I open a ssh connection to '${BOOTSTRAP_IP}' with user 'root' and password 'stratio'
    When I run 'grep -Po '"root_token":\s*"(\d*?,|.*?[^\\]")' /stratio_volume/vault_response | awk -F":" '{print $2}' | sed -e 's/^\s*"//' -e 's/"$//'' in the ssh connection and save the value in environment variable 'vaultToken'
    And I run 'curl -X GET -fkL --tlsv1.2 -H "X-Vault-Token:!{vaultToken}" "https://${VAULT_HOST}:${VAULT_PORT}/v1/${VAULT_USERLAND_CERTIFICATE_BASE_PATH:-userland/certificates/}cassandrastratio" | jq '.data."node-0-server.cassandrastratio_crt"' | sed 's/-----BEGIN CERTIFICATE-----/-----BEGIN CERTIFICATE-----\n/g' | sed 's/-----END CERTIFICATE-----/\n-----END CERTIFICATE-----/g' | sed 's/-----END CERTIFICATE----------BEGIN CERTIFICATE-----/-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----/g' | sed 's/\"//g' > target/test-classes/cassandrastratio.pem' locally
    And I run 'curl -X GET -fkL --tlsv1.2 -H "X-Vault-Token:!{vaultToken}" "https://${VAULT_HOST}:${VAULT_PORT}/v1/${VAULT_USERLAND_CERTIFICATE_BASE_PATH:-userland/certificates/}cassandrastratio" | jq '.data."node-0-server.cassandrastratio_key"' | sed 's/-----BEGIN RSA PRIVATE KEY-----/-----BEGIN RSA PRIVATE KEY-----\n/g' | sed 's/-----END RSA PRIVATE KEY-----/\n-----END RSA PRIVATE KEY-----/g' | sed 's/\"//g' > target/test-classes/cassandrastratio.key' locally
#    And I run 'openssl x509 -in target/test-classes/cassandrastratio.pem -inform pem -outform pem -out target/test-classes/cassandrastratio.pem' locally
#    And I run 'openssl pkcs8 -topk8 -inform PEM -outform pem -in target/test-classes/cassandrastratio.key -out target/test-classes/cassandrastratio1.pem -nocrypt' locally
#    And I securely connect to 'Cassandra' cluster at '${CASSANDRA_HOST}' with user 'node-0-server.cassandrastratio' and crt 'target/test-classes/cassandrastratio.pem' and key 'target/test-classes/cassandrastratio.key' certificates

#  And I connect to 'Cassandra' cluster at  '${CASSANDRA_HOST}'

#  Scenario: Create a keyspace in Cassandra
#    Given I create a Cassandra keyspace named 'opera'
#    Then a Cassandra keyspace 'opera' exists
#
#  Scenario: Check keyspace does not exists
#    Then a Cassandra keyspace 'invalidKeyspace' does not exist
#
#  Scenario: Create a table in Cassandra
#    And I create a Cassandra table named 'analyzertable' using keyspace 'opera' with:
#      |name  | comment |lucene |
#      | TEXT |TEXT     |TEXT   |
#      |  PK  |         |       |
#
#    And I insert in keyspace 'opera' and table 'analyzertable' with:
#      |name 	    |comment				        |
#      |'Kurt'      	|'Hello to a man'   			|
#      |'Michael'    |'Hello to a woman'			    |
#      |'Louis'     	|'Bye to a man' 				|
#      |'John'     	|'Bye to a woman'  			    |
#      |'James'     	|'Hello to a man and a woman'  	|
#
#    Then a Cassandra keyspace 'opera' contains a table 'analyzertable'
#    And a Cassandra keyspace 'opera' contains a table 'analyzertable' with '5' rows
#
#  Scenario: Check table does not exist
#    Then a Cassandra keyspace 'opera' does not contain a table 'invalidTable'
#
#  Scenario: Querying table in Cassandra
#    When a Cassandra keyspace 'opera' contains a table 'analyzertable' with values:
#      |  comment-varchar |
#
#  Scenario: I remove all data
#    Given I drop a Cassandra keyspace 'opera'
#
#  Scenario: Exception in query
#    When I create a Cassandra keyspace named 'opera'
#    And I create a Cassandra table named 'location' using keyspace 'opera' with:
#      | place  | latitude | longitude |lucene |
#      | TEXT   | DECIMAL  |  DECIMAL  |TEXT   |
#      |  PK    | PK       |           |       |
#    And I insert in keyspace 'opera' and table 'location' with:
#      |latitude|longitude|place       |
#      |2.5     |2.6      |'Madrid'    |
#      |12.5    |12.6     |'Barcelona' |
#
#    Given I execute a query over fields '*' with schema 'schemas/geoDistance.conf' of type 'string' with magic_column 'lucene' from table: 'location' using keyspace: 'opera' with:
#      | col          | UPDATE  | geo_point  |
#      | __lat        | UPDATE  | 0          |
#      | __lon        | UPDATE  | 0          |
#      | __maxDist    | UPDATE  | 720km      |
#      | __minDist    | UPDATE  | -100km     |
#    Then an exception 'IS' thrown with class 'Exception' and message like 'InvalidQueryException'
#
#  Scenario: Truncate table in Cassandra
#    Given I truncate a Cassandra table named 'analyzertable' using keyspace 'opera'
#
#  Scenario: Drop table in Cassandra
#    Given I drop a Cassandra table named 'analyzertable' using keyspace 'opera'
#
#  Scenario: Drop keyspace in Cassandra
#    Given I drop a Cassandra keyspace 'opera'