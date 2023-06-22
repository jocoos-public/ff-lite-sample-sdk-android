# FlipFlop SDK Lite for Android

The sample app for FlipFlop Lite SDK 

## Introduction

 - This repository provides live streaming samples to demonstrate how to use the FlipFlop Lite SDK

## Quick start

 - Run the sample server before building sample app for sdk
   - Reference [sample server](https://github.com/jocoos-public/ff-lite-sample-app-server)
 - change "SAMPLE_SERVER_DOMAIN" in app level 'build.gradle' file to the ip(or domain) of your sample server
   - The app needs to connect to the sample server
 - Build and run the sample app

## Project structure

 - main
   - MainFragment
   - MainListFragment : showing live and vod list
 - live
   - StreamingFragment : to do live streaming
   - StreamingPrepareFragment
   - StreamingLiveFragment
   - CameraOptionFragment : to show camera functions
   - MoreOptionFragment : to show add-hoc functions
   - StreamingViewFragment : to watch live
 - vod
   - PlayerVodFragment : to view VOD
 - api
   - apiManager : connect to the sample server
 