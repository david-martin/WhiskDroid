# Android app for calling OpenWhisk actions (on OpenShift)

## Prerequisites

* OpenWhisk installed https://github.com/projectodd/openwhisk-openshift#installation
* OpenWhisk operator installed https://github.com/pb82/serverless-operator#installation-on-openshift
* A `ServerlessAction` created

## Setup

Get the openwhisk json config for an action using below command, saving it to the assets directory.

```
ACTION_NAME=testaction
oc get serverlessaction $ACTION_NAME \
  --template='{"url":"{{index .metadata.annotations "openwhisk.action/url"}}","host":"{{index .metadata.annotations "openwhisk.action/host"}}","name":"{{index .metadata.annotations "openwhisk.action/name"}}","namespace":"{{index .metadata.annotations "openwhisk.action/namespace"}}","username":"{{.spec.username}}","password":"{{.spec.password}}"}' \
  > app/src/main/assets/openwhisk.json
```

Then build and deploy the App to an emulator or device.

