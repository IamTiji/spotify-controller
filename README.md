# Spotify Controller
Control your spotify playback without alt-tabbing

If you found bugs, please report to issue tracker on github!

﻿### When reporting an issue...

*Please make sure to:*
 - **Be specific**, I don't mind title like " doesn't work", but you should explain it as much as possible in its inner text. Adding images to your issue also helps.
 - **Be nice**, some people doesn't even write full sentences. I don't mind if you didn't follow other rules, but you must be respectful. 
 - **Upload your logs**, it is completely impossible to figure out what went wrong even if you explain it really well.  

### Maintaince only
I won't be adding new features to this mod. This does not mean that I will leave this mod broken if there is bugs, but just that there will not be any new feature. I will still try my best to help you when there is an issue, but feature requests will not be fulfilled. This is because I don't feel motovated to develop anymore with Spotify actively trying to kill this kind of programs. There is unstable features on Github that didn't make it to release, people who wants them can build them themselves.

### Spotify Developer Terms Update
Starting from Feb 11, all new users needs to have Premium subscription. From March 9, all free account's connections are disbanded and won't work anymore. For people with premium, you won't be affected at all. [Read more here.](https://developer.spotify.com/blog/2026-02-06-update-on-developer-access-and-platform-security)

### Features
- View what you are playing
- Resume / Pause your song
- Skip to next song
- Listen to previous song (again)
- Seek to wanted position
- Search for a song

### Applied Picture

![In game screenshot](https://cdn.modrinth.com/data/cached_images/0cd0a61c28fd4b1f6f4c44b4b5cd631c04e5319d_0.webp)

### How to set up

Follow [official wiki on creating app](https://developer.spotify.com/documentation/web-api/concepts/apps). Set `Redirect URI` to `http://127.0.0.1:25566/callback`. Press Z to show pop up with button that redirects you to setup page. On the bottom of the page, there are inputs for Client ID and Client Secret. Paste Client ID and Client Secret you got from creating app into input. Sign in normally. After this, you may press Z again to control your playback.

### Future plans

- Ability to view lyrics
- Ability to view queue
- ~~Easier setup~~ *(Impossible since April 15, 2025, [see blog post from Spotify](https://developer.spotify.com/blog/2025-04-15-updating-the-criteria-for-web-api-extended-access))*
- [#28: Volume control](https://github.com/IamTiji/spotify-controller/issues/28)
- [#23: System Media for free account or pre-setup](https://github.com/IamTiji/spotify-controller/issues/23)
- [#14: Mini Player](https://github.com/IamTiji/spotify-controller/issues/14)
- [#14: Like and dislikes](https://github.com/IamTiji/spotify-controller/issues/14)

### License

The project code and asset has different license. 

Code is licensed under [`CC-BY-SA-NC 4.0 International`](LICENSE_CODE)

Image assets is licensed under [`CC-BY 4.0 International`](LICENSE_ASSETS)