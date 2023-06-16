# FlipFlop SDK Lite for Android

FlipFlop Lite SDK

## Introduction

 - This repository provides live streaming samples to demonstrate how to use the FlipFlop Lite SDK

## Before you start

 - You should read manual below before you try this sample app
   - [Getting Started with FlipFlop](https://jocoos.gitbook.io/jocoos-sdk/group-1/flipflop-lite/getting-started-with-flipflop)
 - There are some keys to work sample app
   - appId, streamKey, chatToken, channelKey and so on
   - reference doc below
     - [App API Documentation](https://jocoos.gitbook.io/jocoos-sdk/group-1/flipflop-lite/app-api-documentation)
   - How to get it
     1. You need to use FlipFlop Lite server api to get some keys
        - recommend you to implement it on your server : your server <--> FlipFlop Lite server
     2. Your mobile app need to get it from your server : mobile app <--> your server

## Project structure

 - live
   - StreamingFragment : to do live streaming
   - StreamingPrepareFragment
   - StreamingLiveFragment
   - CameraOptionFragment
   - MoreOptionFragment
   - StreamingViewFragment : to watch live
 - vod
   - PlayerVodFragment : to view VOD
 