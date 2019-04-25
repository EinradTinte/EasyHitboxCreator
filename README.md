# About

I wrote a small Hitbox class consisting of rectangles and circles for a lightweight but still quiet precise collision detection in my game. Instead of one bounding box or a costly polygon shape that would be overkill for my needs I would combine simple shapes to one hitbox. This worked great and the data could be read from file. But creating that data was a tedious thing to do as you would need to set every attribute for every rectangle (x, y, width, height) and circle (x, y, radius) by hand while somehow measuring where on your object it would be.

```
<hitbox name="cactus">
	<rectangle x="28" y="73" width="35" height="37"/>
	<rectangle x="70" y="58" width="42" height="23"/>
	<rectangle x="5" y="109" width="34" height="32"/>
	<circle x="46" y="43" radius="31"/>
</hitbox>
```

_Enter the easy 2D hitbox creator, a visual tool for creating such hitboxes in no time._

Load the image of the object you want to create a hitbox for, add as many rectangles and circles as you need and the tool will automatically convert their attributes to text you can simply copy & paste into your game files.
But the best part: The output is fully customizable. So whatever form you need that data in just change the format settings and there you go.

# Features

Simply add, move and resize "hitshapes" with your mouse.

![](https://user-images.githubusercontent.com/46963081/56773433-5fc98400-67be-11e9-8bd6-f0cce0ad4519.gif)

Fully customizable output with live preview to tailor the data to your needs.

![](https://user-images.githubusercontent.com/46963081/56773464-87b8e780-67be-11e9-8287-8f7e0406c4db.gif)

Save projects for later editing.

### Credits
Shoutout to crashinvaders awesome [texture-packer](https://github.com/crashinvaders/gdx-texture-packer-gui) which I used as a reference in regards of design and various project managing mechanisms.
