Operations
==========

List: curl -u test -X GET "http://localhost:8080/o/rest/api/users[?page&per_page]" -H "Accept: application/json"

getOne: curl -u test -X GET "http://localhost:8080/o/rest/api/users/:id" -H "Accept: application/json"

update profile: curl -u test -X PUT "http://localhost:8080/o/rest/api/users/:id/portrait" -H "Accept: application/json" -H "Content-Type: image/*" --data-binary @image_file

create: curl -u test -X POST "http://localhost:8080/o/rest/api/users" -H "Accept: application/json" -H "Content-Type: application/json" --data-ascii "{prefix: 'Sr', firstName: 'Carlos', lastName: 'Sierra', screenName: 'csierra', emailAddress: 'csierra@liferay.com'}"

list prefixes: curl -X GET "http://localhost:8080/o/rest/api/users/prefixes" -H "Accept: application/json"