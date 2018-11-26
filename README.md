# BetterDeploymentsDemo

This repository contains the source code of the demo part of the `Better Deployments with Sub Environments Using Spring Cloud and Netflix Ribbon` webinar.

https://www.brighttalk.com/webcast/14893/342125?utm_campaign=webcasts-search-results-feed&amp;utm_content=better%20deployments&amp;utm_source=brighttalk-portal&amp;utm_medium=web

## Enviorment Setup

This demo requieres `Consul` to run on the host where services should run. To run Consul on the machine in `dev` mode us the following command:

`
docker run --net=host -d --name=dev-consul consul consul agent -dev -client=0.0.0.0
`



