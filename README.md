# MinecraftGUI

MinecraftGUI is a simple plugin that permit to create graphical element on the minecraft client. It is very easy to use for the developpers and the players only have to download the mod.

## How MinecraftGUI work:
Everything is a component. Every elements on the screen the player can see or interact is a component. A component can contain some components. Each component has attributes and that allow to change their aspect. The possibility to interact with the component give you the possibility to change set their attributes when they are clicked, hovered by the mouse and when nothing happen.


## All the possibility with MinecraftGUI:
  - Draw rectangle.
  - Draw image(Minecraft items and blocks and custom image).
  - Interact with some components(Click and write).
  - The player can send forms to the server with information.
  - Link button with the web browser.
  - Event system to support when the player click on the components.
  - Authentication system to check if it is the good player that send information.


## How to use MinecraftGUI:
  - 1: You need to create a class that will extend of djxy.models.ComponentManager.
  - 2: You have to register your ComponentManager class. You can register your ComponentManager anytime when this events are called PreInitializationEvent, InitializationEvent, PostInitializationEvent, LoadCompleteEvent and ServerAboutToStart. When one of this events is called, you will have to call the function djxy.controllers.MainController.getInstance().addComponentManager([Your object ComponentManager]).
  

## All the type of components to use:
  - Panel
    - Draw a rectangle.
  - Image
    - Draw an image from Minecraft or a custom image.
  - Paragraph
    - Draw a text.
  - Button
    - There is two type of button:
      - Normal: When clicked, that will send a form to the server that will contain all the inputs linked.
      - Url: When clicked, that will send a form and that will open the default web browser of the player on a website.
  - List
    - The list is to add as many component as you want and it will draw the components in order of addition. It will contain       multiple pages to show all the components.
  - Input
    - There is five type of input:
      - Normal: The player can write anything he want.
      - Password: Everything the player will write on the screen will appear like that *.
      - Numeric: The player can write a double or an integer.
      - Numeric no decimal: The player can only write an integer.
      - Invisible: This input will serve to store data and it will not be seen by the player.
