# About SushiChefGame
I made this basic game throughout the semester in my Advanced Programming in Java class. This game allows you to create sushi
and compete against other chefs (bots) in trying to get customers (also bots) to eat your sushi.

# How to Use
The user starts off the game. This is done by hitting the "Make some Sushi!" button in the top right corner of the window. Go 
through the steps of making sushi. Mouse over a space at any time to see its position. If the sushi is successfully placed, 
the color of your plate will appear in the position you specified, otherwise it will give you an error at the bottom left of the 
window. The cost of making the sushi will then be subtracted from your initial balance ($100). Hit the rotate button in the bottom
left of the window to make one rotation (move sushi over one space) and see if a customer eats the sushi. If the sushi is eaten,
the plate will be removed from the screen and the price of that sushi will be added to your balance. Note that once you hit rotate,
the chefs will also begin randomly making and placing plates. If a plate is determined to be spoiled (each plate and sushi combo
has different spoilage times, meaning it has been on the sushi belt (the ciruclar layout on screen) too long), it will also be 
removed from the screen. Do this over and over. There is no end to the game so play as much as you want. Sort the chefs and user
by balance, amount consumed, or amount spoiled at any time by clicking on one of the three buttons in the top left of the window.

# How it Works
The game in its entirety is too complex to get into as it has so many classes relying on each other. However, the game was designed
in the MVC pattern and stays true to this design in all classes.

# Other Info About the Program
I programmed approximately 3/4 of the classes associated with this program. My professor gave me approx 1/4 of the classes for
the final game (mostly classes involving the chefs and customers). I am most proud of the SushiMakerWindow class as it controls
the functionality for the window that appears when the "Make some Sushi!" button is clicked. This class contains all the code
for the window that appears and all the possible choices that could be made. I by far spent the most time on this class; I made
this class completely from scratch, following no guidelines for this class, I made it simply because I deemed it the easiest way
to make the sushi to go along with the design I decided on for the program. 

# The jar file is the executable jar for this program so download that if you just wish to use the program.

# Enjoy!
![alt text](https://media.giphy.com/media/Qjt1pbcM5vMPe/giphy.gif)
