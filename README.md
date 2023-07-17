<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Recipes

[![REUSE Compliance](https://github.com/noi-techpark/webcomp-recipe/actions/workflows/reuse.yml/badge.svg)](https://github.com/noi-techpark/odh-docs/wiki/REUSE#badges)
[![REUSE status](https://api.reuse.software/badge/github.com/noi-techpark/webcomp-recipe)](https://api.reuse.software/info/github.com/noi-techpark/webcomp-recipe)
[![CI/CD](https://github.com/noi-techpark/webcomp-recipe/actions/workflows/main.yml/badge.svg)](https://github.com/noi-techpark/webcomp-recipe/actions/workflows/main.yml)

A webcomponent to list recipes and show all details of a recipe. It is possible
to display the recipes as cards or as a list and it exists a recipe search
functionality. In addition it can be displayed only selected items or the
details page of a single one.

Do you want to see it in action? Go to our [web component store](https://webcomponents.opendatahub.com/webcomponent/32c2bef1-a691-4b93-bc41-56ecb88e39f0)!

- [Recipes](#recipes)
  - [Usage](#usage)
    - [Attributes](#attributes)
      - [content-id-list](#content-id-list)
      - [language](#language)
      - [page-size](#page-size)
      - [default-presentation](#default-presentation)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Source code](#source-code)
    - [Dependencies](#dependencies)
    - [Build](#build)
  - [Tests and linting](#tests-and-linting)
  - [Deployment](#deployment)
  - [Docker environment](#docker-environment)
    - [Installation](#installation)
    - [Dependenices](#dependenices)
    - [Start and stop the containers](#start-and-stop-the-containers)
    - [Running commands inside the container](#running-commands-inside-the-container)
  - [Information](#information)
    - [Support](#support)
    - [Contributing](#contributing)
    - [Documentation](#documentation)
    - [Boilerplate](#boilerplate)
    - [License](#license)

## Usage

```html
<script src="./odh-recipe.min.js"></script>


<odh-recipe></odh-recipe>
```

### Attributes

#### content-id-list

List of content Ids separated by a comma. Example: "1,2,3"

Type: String\
Default: null

This is the article ID inside the Open Data Hub Tourism API. You can use the
following command to retrieve IDs:

```http
GET https://tourism.opendatahub.com/api/ArticleReduced
    ?language=it
    &articletype=rezeptartikel
```

Make sure to put the correct language. The IDs are sometimes only available in a
certain language, and therefore you might get an empty result.

See https://tourism.opendatahub.com/swagger/ui/index#/Article for details.

#### language

Language of the data

Type: string\
Options: ["de", "en", "it"]\
Default: "de"

#### page-size

Number of items that are shown in one page

Type: Number\
Default: 20

#### default-presentation

Default presentation of recipe list

Type: string\
Options: ["list", "grid"]\
Default: "list"

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- ToDo: Check the prerequisites
- Node 12 / NPM 6

For a ready to use Docker environment with all prerequisites already installed
and prepared, you can check out the [Docker environment](#docker-environment)
section.

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/webcomp-recipe.git
```

Change directory:

```bash
ToDo: cd webcomp-recipe/
```

### Dependencies

Download all dependencies:

```bash
npm install
```

### Build

Build and start the project:

```bash
npm run start
```

The application will be served and can be accessed at
[http://localhost:8080](http://localhost:8080).

## Tests and linting

The tests and the linting can be executed with the following commands:

```bash
npm run lint
```

!! There are currently no tests available.

## Deployment

To create the distributable files, execute the following command:

```bash
npm run build
```

After the build the web-component can be tested by opening /dist/demo.html

## Docker environment

For the project a Docker environment is already prepared and ready to use with
all necessary prerequisites.

These Docker containers are the same as used by the continuous integration
servers.

### Installation

Install [Docker](https://docs.docker.com/install/) (with Docker Compose) locally
on your machine.

### Dependenices

First, install all dependencies:

```bash
docker-compose run --rm app /bin/bash -c "npm install"
```

### Start and stop the containers

Before start working you have to start the Docker containers:

```
docker-compose up --build --detach
```

After finished working you can stop the Docker containers:

```
docker-compose stop
```

### Running commands inside the container

When the containers are running, you can execute any command inside the
environment. Just replace the dots `...` in the following example with the
command you wish to execute:

```bash
docker-compose run --rm app /bin/bash -c "..."
```

Some examples are:

```bash
docker-compose run --rm app /bin/bash -c "npm run test"
```

## Information

### Support

For support, please contact [help@opendatahub.com](mailto:help@opendatahub.com).

### Contributing

If you'd like to contribute, please follow the Contributor Guidelines that can be found at [https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines%3A-Getting-started](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines%3A-Getting-started).

### Documentation

More documentation can be found at
[https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### Boilerplate

The project uses this boilerplate:
[https://github.com/noi-techpark/webcomp-boilerplate](https://github.com/noi-techpark/webcomp-boilerplate).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE
Version 3 license. See the [LICENSE.md](LICENSE.md) file for more information.

### REUSE

This project is [REUSE](https://reuse.software) compliant, more information about the usage of REUSE in NOI Techpark repositories can be found [here](https://github.com/noi-techpark/odh-docs/wiki/Guidelines-for-developers-and-licenses#guidelines-for-contributors-and-new-developers).

Since the CI for this project checks for REUSE compliance you might find it useful to use a pre-commit hook checking for REUSE compliance locally. The [pre-commit-config](.pre-commit-config.yaml) file in the repository root is already configured to check for REUSE compliance with help of the [pre-commit](https://pre-commit.com) tool.

Install the tool by running:
```bash
pip install pre-commit
```
Then install the pre-commit hook via the config file by running:
```bash
pre-commit install
```
