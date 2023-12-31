# FlipFlop Lite SDK for Android

The sample app for FlipFlop Lite SDK 

## Introduction

 - This repository provides live streaming samples to demonstrate how to use the FlipFlop Lite SDK

## Before you start sample app

 - For live streaming and watching via the SDK, you need to get an access token from the FlipFlop Lite server.
 - You need to implement your own server to get an access token from the FlipFlop Lite server so that you can pass access token to the client app.
 - Get access token from FlipFlop Lite server
   - [Member Login](https://jocoos-public.github.io/dev-book/jekyll/2023-10-16-App-Member-API.html#member-login)
   - set access token to 'accessToken' variable of MainFragment
 - Get video list from FlipFlop Lite server
   - [Get VideoRooms](https://jocoos-public.github.io/dev-book/jekyll/2023-10-16-Member-VideoRoom-API.html#get-videorooms)
   - set video list in MainListFragment

## Quick start

 - Refer to the [SDK Document](https://jocoos-public.github.io/dev-book/jekyll/2023-10-18-Android_1_Quick_Start.html)

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
 