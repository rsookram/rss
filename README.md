# rss

An RSS app for Android.

This app allows you to subscribe to RSS feeds and aggregates them into a single
list for viewing. Viewing the contents of an entry is delegated to another app
(an app that handles a `VIEW` intent, usually a browser). There is no read /
view status tracked by this app, and no server-side component to sync data
with.


## Features

- Aggregates RSS feeds
- Requires only the `INTERNET` permission, to fetch feeds
- Automatically refreshes content daily
- Automatically prunes entries older than 90 days
- Small app (~2 MB)


## License

```
Copyright 2021 Rashad Sookram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
