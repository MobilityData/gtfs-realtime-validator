# Instructions to download the latest snapshot version of the validator

The GitHub Action [`test_package.yml`](../.github/workflows/test_package.yml) is responsible for automatically testing and packaging the project on each commit of each branch. Artifacts are stored for 90 days.

The latest snapshot builds of the validator library and webapp can be found under [the list of artifacts for the master branch](https://github.com/MobilityData/gtfs-realtime-validator/actions/workflows/test_package.yml?query=branch%3Amaster):

Library - gtfs-realtime-validator-lib-v-*-SNAPSHOT.jar
Webapp - gtfs-realtime-validator-webapp-v-*-SNAPSHOT.jar

...where * is the branch and SHA1 commit of the build.

See the following instructions to download the artifact:

1. Go to the ["Test and Package" Actions for the master branch](https://github.com/MobilityData/gtfs-realtime-validator/actions/workflows/test_package.yml?query=branch%3Amaster) for this repository. You can also browse to this page by clicking as shown in the below image:
![access actions](https://user-images.githubusercontent.com/46797220/154085290-89666a47-4825-465d-9b56-83331b5aa363.png)
These three aforementioned steps can be skipped if you directly go to the following url: [Test Package workflow executions on master branch](https://github.com/MobilityData/gtfs-realtime-validator/actions/workflows/test_package.yml?query=branch%3Amaster)

2. Select the first item in the list: it is the latest iteration of the workflow that was run on the master branch
3. Click on the artifact's name that is needed to start download 
![download artifacts](https://user-images.githubusercontent.com/46797220/154085256-dce24278-a574-41bc-bbf5-45a4ba02df3c.png)
4. Extract the JAR file from ZIP archive obtained by downloading the artifact
