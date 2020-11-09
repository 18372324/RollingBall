# Rolling Ball —— alpha version
A sensor game

## Demo
![game screenshot](https://github.com/18372324/RollingBall/blob/main/screenShots/GameStart.png?raw=true)

## GamePlay and Help
![Player pic](https://github.com/18372324/RollingBall/blob/main/screenShots/ball_normal.png?raw=true) Player: You need to rotates the phone to control the ball to move and dodge bullets.  


![Turret pic](https://github.com/18372324/RollingBall/blob/main/screenShots/turret_down.png?raw=true) Turret: The turret will appear in a random location and shoot a bullet.  


![DamageBullet pic](https://github.com/18372324/RollingBall/blob/main/screenShots/bullet_damage.png?raw=true) Damage-type bullet: Once the player is hit by this type bullet, he will be killed.  


![SlowBullet pic](https://github.com/18372324/RollingBall/blob/main/screenShots/bullet_slow.png?raw=true) Slow-type bullet: This kind of bullet will bring the player a debuff of about 5s, namely, slow down. In addition, this bullet is faster than the damage-type bullet.  


![PlyaerSlow pic](https://github.com/18372324/RollingBall/blob/main/screenShots/ball_slow.png?raw=true) Player with "slow down" debuff.  

## Alpha version Done
The player and bullet can move on the screen freely. The collision detections between player and bullets, and between the collision bodies (include player and bullets) and screen boundaries have been finished as well. Moreover, I realized the function that the bullet will move towards the direction of the player initially through the algorithm.   
However, due to time constraints, the menu shown at the end of the game is only drawn, while the finger touch event response is not implemented (TODO). Also, the current score of each game will be displayed, while the highest score is not recorded in local (TODO).  

## Plan

- Week 10: Feat: finish the game-over menu & draw the effect of the player being hit by a bullet. Test: code Review.  
- Week 11: Feat: the game difficulty level up & the Main menu interface of this game (including game start, help, Rank board, and quit game). Test: code Review.
- Week 12: Feat: integrate the Main menu interface and gaming interface & Make the interface more attractive. Test: code Review.