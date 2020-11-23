# Rolling Ball —— alpha version
A sensor game

## Demo
![game screenshot](https://github.com/18372324/RollingBall/blob/main/screenShots/GameStart.jpg?raw=true)

## GamePlay and Help
![Player pic](https://github.com/18372324/RollingBall/blob/main/screenShots/ball_normal.png?raw=true) Player: You need to rotates the phone to control the ball to move and dodge bullets.  


![Turret pic](https://github.com/18372324/RollingBall/blob/main/screenShots/turret_down.png?raw=true) Turret: The turret will appear in a random location and shoot a bullet.  


![DamageBullet pic](https://github.com/18372324/RollingBall/blob/main/screenShots/bullet_damage.png?raw=true) Damage-type bullet: Once the player is hit by this type bullet, he will be killed.  


![SlowBullet pic](https://github.com/18372324/RollingBall/blob/main/screenShots/bullet_slow.png?raw=true) Slow-type bullet: This kind of bullet will bring the player a debuff of about 5s, namely, slow down. In addition, this bullet is faster than the damage-type bullet.  


![PlyaerSlow pic](https://github.com/18372324/RollingBall/blob/main/screenShots/ball_slow.png?raw=true) Player with "slow down" debuff.  


![Pause pic](https://github.com/18372324/RollingBall/blob/main/screenShots/pause1.png?raw=true) When game is being played, pause the game is acceptable. The player can touch **this button or Virtual Back Key**  


![Level up pic](https://github.com/18372324/RollingBall/blob/main/screenShots/levelup.png?raw=true) Level up: **Pay attention to this prompt**. The game is going to become much more difficult (Maybe the number of turret increases, or the bullets speed up ), as soon as this prompt appears. Whatever, its appearance means that this game is no longer as simple as you think.

## Alpha version Done
The player and bullet can move on the screen freely. The collision detections between player and bullets, and between the collision bodies (include player and bullets) and screen boundaries have been finished as well. Moreover, I realized the function that the bullet will move towards the direction of the player initially through the algorithm.   
However, due to time constraints, the menu shown at the end of the game is only drawn, while the finger touch event response is not implemented (TODO). Also, the current score of each game will be displayed, while the highest score is not recorded in local (TODO).  

## Beta version Done

- The Main Menu UI.  This user interface is composited by a textview and surfaceview. The function of textview is to show the title with a rainbow style (Dynamic style), while the surfaceview is responsible for the response events of the buttons.
- The Connection of Main Menu UI and Game View.
- Store the highest score in local file.
- The difficulty of the game increases with the score. This is implemented by increasing the number of turrets (Increase in the order of top, bottom, left and right). Currently, Difficulty increases every ten scores (I call it LEVEL_THRESHOLD). You can change the difficulty of the game by setting this constant in the class GameData.
- Adding some new buttons and prompts (like the "level up") in Game View to make the game more accessible to players.  

## Plan For Final
**Develop a new game mode —— Adventure mode.**  
In this mode, the player will control the ball to avoid the trap and walk out of the maze.
- Week 13: Learn Tile Map and develop a tile map maker by myself.
- Week 14: Design the maze and generate the map.
- Week 15: Integrate the whole game. And Code Review.

## Reference
Reference:
* Some the images are available at: http://box.ptpress.com.cn/y/978-7-115-47555-8
* Others are drawn by myself

## My Github
More details are available at: [Rolling Ball at Github 18372324](https://github.com/18372324/RollingBall)  