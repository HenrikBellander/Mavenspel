import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();

        for (int i = 0; i < 24; i++) {                      //Målar bakgrunden
            for (int j = 0; j < 80; j++) {
                terminal.setBackgroundColor(TextColor.ANSI.WHITE);
                terminal.setCursorPosition(j, i);
                terminal.putCharacter(' ');
            }
            terminal.flush();
        }

        terminal.setForegroundColor(TextColor.ANSI.BLACK);            //Placerar bomb
        Random r = new Random();
        /*Position bomb = new Position(r.nextInt(80), r.nextInt(24));
        terminal.setCursorPosition(bomb.getX(), bomb.getY());
        terminal.putCharacter('@');*/

        final char block = '\u2588';

        List<Position> food = new ArrayList<>();                    //Placerar mat
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        for (int i = 0; i < 30; i++) {
            food.add(new Position(r.nextInt(80), r.nextInt(24)));
        }
        for (Position o : food) {
            terminal.setCursorPosition(o.getX(), o.getY());
            terminal.putCharacter('\u2618');            //25CF
        }
        terminal.setForegroundColor(TextColor.ANSI.BLACK);
        List<Position> walls = new ArrayList<>();                   //Placerar walls
        for (int i = 0; i < 30; i++) {
            walls.add(new Position(r.nextInt(80), r.nextInt(24)));
        }
        for (Position o : walls) {
            terminal.setCursorPosition(o.getX(), o.getY());
            terminal.putCharacter(block);
        }
        terminal.flush();


        Position horseMonster = new Position(79,23);
        Position footMonster = new Position(79, 0);

        int x = 10;                                 //Initierar player char
        int y = 10;
        char player = '\u2655';
        int points = 0;

        terminal.setCursorVisible(false);
        boolean continueReadingInput = true;
        int oldX = x;
        int oldY = y;
        int oldMonsterX = horseMonster.getX();
        int oldMonsterY = horseMonster.getY();
        int oldFootX = footMonster.getX();
        int oldFootY = footMonster.getY();



        do {
            terminal.setCursorPosition(oldX, oldY);             //Ta bort vår gamla, sätt dit vår nya position
            terminal.putCharacter(' ');
            terminal.setCursorPosition(x, y);
            terminal.putCharacter(player);
            oldX = x;                                           //Vår gamla position innan den ändras
            oldY = y;


            terminal.setCursorPosition(oldMonsterX, oldMonsterY);   //Ta bort monstrets gamla position
            terminal.putCharacter(' ');

            for (Position o : walls) {
                terminal.setCursorPosition(o.getX(), o.getY());
                terminal.putCharacter(block);
            }

            terminal.setCursorPosition(horseMonster.getX(), horseMonster.getY()); //Skriv ut monstrets position
            terminal.putCharacter('\u265e');

            oldMonsterX = horseMonster.getX();
            oldMonsterY = horseMonster.getY();

            terminal.setCursorPosition(oldFootX, oldFootY);
            terminal.putCharacter(' ');
            terminal.setCursorPosition(footMonster.getX(), footMonster.getY()); //Skriv ut monstrets position
            terminal.putCharacter('\u26c7');

            oldFootX = footMonster.getX();
            oldFootY = footMonster.getY();

            terminal.flush();



            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);

            KeyType type = keyStroke.getKeyType();




            switch (type) {                                     //Vår förflyttning
                case ArrowLeft:
                    if (x == 0) {
                        x = 79;
                    } else {
                        x--;
                    }
//                    player = '\u2190';
                    break;
                case ArrowRight:
                    if (x == 79) {
                        x = 0;
                    } else {
                        x++;
                    }
//                    player = '\u2192';
                    break;
                case ArrowDown:
                    if (y == 23) {
                        y = 0;
                    } else {
                        y++;
                    }
//                    player = '\u2193';
                    break;
                case ArrowUp:
                    if (y == 0) {
                        y = 23;
                    } else {
                        y--;
                    }
//                    player = '\u2191';
            }
            terminal.flush();


            for (Position o : walls) {
                if (o.getX() == x && o.getY() == y) {
                    x = oldX;
                    y = oldY;
                    break;
                }
            }




            if (horseMonster.getX() > x){
                horseMonster.setX(horseMonster.getX()-1);
            } else if (horseMonster.getX() < x){
                horseMonster.setX(horseMonster.getX()+1);
            }
            if (horseMonster.getY() > y){
                horseMonster.setY(horseMonster.getY()-1);
            } else if (horseMonster.getY() < y){
                horseMonster.setY(horseMonster.getY()+1);
            }

            if (footMonster.getX() > x){
                footMonster.setX(footMonster.getX()-1);
            } else if (footMonster.getX() < x){
                footMonster.setX(footMonster.getX()+1);
            }
            if (footMonster.getY() > y){
                footMonster.setY(footMonster.getY()-1);
            } else if (footMonster.getY() < y){
                footMonster.setY(footMonster.getY()+1);
            }

            for (Position o : walls) {
                if ((o.getX() == footMonster.getX() && o.getY() == footMonster.getY()) ||  (horseMonster.getX() == footMonster.getX() && horseMonster.getY() == footMonster.getY())){
                    footMonster.setX(oldFootX);
                    footMonster.setY(oldFootY);
                    break;
                }
            }



            //Character c = keyStroke.getCharacter();


            terminal.flush();




            for (Position o : food){
                if (o.getX() == x && o.getY() == y) {
                    points++;
                    food.add(new Position(r.nextInt(80), r.nextInt(24)));
                    terminal.setCursorPosition(food.get(food.size()-1).getX(), food.get(food.size()-1).getY());
                    terminal.setForegroundColor(TextColor.ANSI.GREEN);
                    terminal.putCharacter('\u2618');
                    terminal.setForegroundColor(TextColor.ANSI.BLACK);
                    break;
                }
            }
            //Death+message
            if ((x == horseMonster.getX() && y == horseMonster.getY()) || x == footMonster.getX() && y == footMonster.getY()) {
                continueReadingInput = false;
                terminal.clearScreen();
                terminal.setCursorPosition(35, 12);
                terminal.setBackgroundColor(TextColor.ANSI.BLACK);
                terminal.setForegroundColor(TextColor.ANSI.RED);
                terminal.bell();
                String death = "YOU DIED!";
                for (int i = 0; i < death.length(); i++) {
                    terminal.putCharacter(death.charAt(i));
                }
                terminal.flush();
            }
            //End of death
            if (points == 10){
                continueReadingInput = false;
                //terminal.clearScreen();
                terminal.setCursorPosition(35, 12);
                terminal.setBackgroundColor(TextColor.ANSI.WHITE);
                terminal.setForegroundColor(TextColor.ANSI.GREEN);
                String death = "YOU WON!";
                for (int i = 0; i < death.length(); i++) {
                    terminal.putCharacter(death.charAt(i));
                }
                terminal.flush();
            }
            //System.out.println(type);
            //System.out.println(c);
            terminal.flush();
            if (type.equals(KeyType.Escape)) {
                continueReadingInput = false;
            }
            String pts = "Points: " + points;
            terminal.setCursorPosition(0,0);
            for (int i = 0; i < pts.length(); i++) {
                terminal.putCharacter(pts.charAt(i));
            }
            /*System.out.println("Vår position: " + x + ", "+ y);
            System.out.println("Monster: " + horseMonster.getX() + ", " + horseMonster.getY());*/


            terminal.flush();
        } while (continueReadingInput);
    }
}
