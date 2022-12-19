
# user-service

A microservice that focuses on user authorization. It uses keycloak as identity and access management solution.

## Deployment

Before first project install, run docker command to install Kecloak for identity access and management.

```bash
  docker run --name booking-keycloak -p 8083:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:18.0.1 start-dev
```

After this, you can run it from docker desktop by finding booking-keycloak.

To deploy this project run from the project's directory

```bash
  docker compose up --build
```


## Documentation

To access API documentation of this service, run project and go to http://localhost:9000/swagger-ui/index.html 
## Authors

- [@timur-code](https://www.github.com/timur-code)


## Features

- Registration
- Login
- Logout
- Refresh token


## Related

This microservice is a part of bigger picture for the booking diploma project.


booking-service (TBA)
