# Instructions to download the latest snapshot version of the validator

The following workflows produce artifacts that are stored for a period of 90 days:

More particularly, [`test_pack_doc.yml`](../.github/workflows/test_package.yml) is responsible to automatically test and package the project.
This workflow is triggered on each commit of each branch. 

As a result, the latest snapshot version of the validator can be found under the list of artifacts generated when this workflow is executed on the `master` branch.
The application jar is named as "gtfs-realtime-validator-lib-v-*-SNAPSHOT.jar".

This also applies to the latest webapp snapshot version, for which the webapp jar is named as "gtfs-realtime-validator-webapp-v-*-SNAPSHOT.jar".

See the following instructions to download the artifact:

1. Access the actions listing on the project's main page
1. Select `Test Package` in the `Workflows` column
1. Select `master` branch
![access actions](https://user-images.githubusercontent.com/46797220/154085290-89666a47-4825-465d-9b56-83331b5aa363.png)
These three aforementioned steps can be skipped if you directly go to the following url: [Test Package workflow executions on master branch](https://github.com/MobilityData/gtfs-realtime-validator/actions/workflows/test_package.yml?query=branch%3Amaster)

1. Select the first item in the list: it is the latest iteration of the workflow that was run on the master branch
1. Click on the artifact's name that is needed to start download 
![download artifacts](https://user-images.githubusercontent.com/46797220/154085256-dce24278-a574-41bc-bbf5-45a4ba02df3c.png)
