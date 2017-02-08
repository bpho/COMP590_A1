package com.example.bpho.comp590_assignment1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public boolean[] switches = new boolean[10];
    public boolean[] buttons = new boolean[16];
    public static final char[] switchLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
    public ArrayList<Integer> solutionSet = new ArrayList<>();
    public Set<Character> finalSolution = new HashSet<>();
    public boolean autoPressed;
    public boolean blackButtons;
    public boolean randomSwitches;
    public int moveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGrid();
        randomSwitches();
    }

    public void restartButton(View view) {
        // Empty final solution hashset
        finalSolution.clear();
        moveCount = 0;
        autoPressed = false;

        // Reset TextViews
        TextView sequenceCount = (TextView)findViewById(R.id.switchCount);
        sequenceCount.setText("Switches count: ");
        TextView moves = (TextView)findViewById(R.id.moves);
        moves.setText("");

        TextView sequence = (TextView)findViewById(R.id.sequence);
        sequence.setText("Sequence: ");

        startGrid();
        randomSwitches();
        restartSwitches();
    }

    /**
     * When restart button is pressed, switches must also be switched back
     * to original png to avoid confusion
     */
    public void restartSwitches() {
        for (int i = 0; i < switches.length; i++) {
            String switchId = "switch"+Character.toUpperCase(switchLetters[i]);
            int resID = getResources().getIdentifier(switchId, "id", getPackageName());
            Button btn = (Button)findViewById(resID);
            String letterId = "let"+switchLetters[i];
            int resID2 = getResources().getIdentifier(letterId, "drawable", getPackageName());
            btn.setBackgroundResource(resID2);
            if (switches[i] == true) {
                indexSwitch(i);
            }
            switches[i] = false;
        }
    }


    public void resetButton(View view) {
        restartSwitches();
        // TODO:
    }

    /**
     * Prints solution in TextView (the sequence in any order) along with size
     * of the Set used to print the solution
     *
     * No solution just prints "no solution"
     *
     * AUTO: If a letter is even, just remove them
     * if letter is odd, just contain one
     * USE: Map to store key values, REMOVE if Map already contains (aka to avoid evens)
     */
    public void autoFinish(View view) {
        autoPressed = true;
        TextView sequenceCount = (TextView)findViewById(R.id.switchCount);
        sequenceCount.setText("Switches count: (SOLUTION: ");
        sequenceCount.append(Integer.toString(finalSolution.size()));
        sequenceCount.append(") ");


        TextView sequence = (TextView)findViewById(R.id.sequence);
        sequence.setText("Sequence: (SOLUTION: ");
        if (finalSolution.isEmpty()) {
            sequence.append("NO SOLUTION");
        } else {
            for (Character letter : finalSolution) {
                sequence.append(Character.toString(letter));
            }
        }
        sequence.append(")");

//        animateSolution();
    }

    // TODO: Fix timer functionality, only being used for initial delay!
    // TODO: Add switch animation as well
//    public void animateSolution() {
//        final Handler handler = new Handler();
//        for (Character letter : finalSolution) {
//            new Timer().schedule(new MyTimerTask(letter) {
//                @Override
//                public void run() {
//                    handler.post(new Runnable() {
//                        public void run() {
//                            switch (letter) {
//                                case 'A':
//                                    switchA();
//                                    break;
//                                case 'B':
//                                    switchB();
//                                    break;
//                                case 'C':
//                                    switchC();
//                                    break;
//                                case 'D':
//                                    switchD();
//                                    break;
//                                case 'E':
//                                    switchE();
//                                    break;
//                                case 'F':
//                                    switchF();
//                                    break;
//                                case 'G':
//                                    switchG();
//                                    break;
//                                case 'H':
//                                    switchH();
//                                    break;
//                                case 'I':
//                                    switchI();
//                                    break;
//                                case 'J':
//                                    switchJ();
//                                    break;
//                            }
//                        }
//                    });
//                }
//            }, 1000);
//        }
//    }

    public void winningToast() {
        Context context = getApplicationContext();
        CharSequence text = "You win!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Randomizes the board to start either all black or white to create variety
    public void startGrid() {
        Random rand = new Random();
        int n = rand.nextInt(10) + 1;
        if (n > 5) {        // White buttons
            blackButtons = false;
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = false;
                changeNumber(i);
            }
        } else {            // Black buttons
            blackButtons = true;
            for (int j = 0; j < buttons.length; j++) {
                buttons[j] = true;
                changeNumber(j);
            }
        }
    }

    /**
     * Solutions only have a max of 10 possible switches pressed,
     * since 1 --> 3 --> 5 all produce same result as pressing it once.
     * Store in set since order doesn't matter and duplicates can't exist
     *
     */
    public void randomSwitches() {
        Random rand = new Random();
        int randMoves = rand.nextInt(16) + 5;   // 5 - 20 possible moves
        ArrayList<Integer> switchList = new ArrayList<>();
        int counter = 1;
        while (counter<randMoves) {
            int randomSwitch = rand.nextInt(10);    // random switch pressed
            switchList.add(randomSwitch);
            counter++;
        }
        solutionSet = switchList;
        randomSwitches = true;
        for (int randSwitch : switchList) {
            indexSwitch(randSwitch);
        }
        randomSwitches = false;
        storeSolution();
    }

    public void indexSwitch(int i) {
        switch (i) {
            case 0:
                switchA();
                break;
            case 1:
                switchB();
                break;
            case 2:
                switchC();
                break;
            case 3:
                switchD();
                break;
            case 4:
                switchE();
                break;
            case 5:
                switchF();
                break;
            case 6:
                switchG();
                break;
            case 7:
                switchH();
                break;
            case 8:
                switchI();
                break;
            case 9:
                switchJ();
                break;
        }
    }

    // Stores occurrences into a map, adds the switch letter as Uppercase character
    public void storeSolution() {
        Map<Character, Integer> finalSeq = new HashMap<>();
        for (int solutionInt : solutionSet) {
            char switchLetter = Character.toUpperCase(switchLetters[solutionInt]);
            if (finalSeq.containsKey(switchLetter)) {
                finalSeq.put(switchLetter, finalSeq.get(switchLetter) + 1);
            } else {
                finalSeq.put(switchLetter, 1);
            }
        }

        for (Character key : finalSeq.keySet()) {
            if ((finalSeq.get(key) % 2) != 0) {
                Log.v("Printing Map Contents", Character.toString(key));
                finalSolution.add(key);
            }
        }

        Log.v("Printing Map Contents", "---------------------");
    }

    public void updateTextViews(char input) {
        moveCount++;

        TextView sequenceCount = (TextView)findViewById(R.id.moves);
        sequenceCount.setText(Integer.toString(moveCount));

        // Adds scroll bar to exceeding sequence length
        TextView sequence = (TextView)findViewById(R.id.sequence);
        sequence.setMovementMethod(new ScrollingMovementMethod());
        if (moveCount == 1 || autoPressed == true) {
            sequence.append(" ");
            autoPressed = false;
        }
        sequence.append(Character.toString(input));

        // Check if all buttons are either true or false
        if(isSolved(buttons)) winningToast();

    }

    public boolean isSolved(boolean[] btnArray) {
        if (blackButtons == true) {
            for (boolean b : btnArray) {
                if (b) return false;
            }
        } else if (blackButtons == false) {
            for (boolean b : btnArray) {
                if (!b) return false;
            }
        }
        return true;
    }

    /**
     *  Grid color changes along with switch animation
     *  when they are clicked
     */
    public void switchA(View view) {
        switchA();
        updateTextViews('A');
    }

    public void switchB(View view) {
        switchB();
        updateTextViews('B');
    }

    public void switchC(View view) {
        switchC();
        updateTextViews('C');
    }

    public void switchD(View view) {
        switchD();
        updateTextViews('D');
    }

    public void switchE(View view) {
        switchE();
        updateTextViews('E');
    }

    public void switchF(View view) {
        switchF();
        updateTextViews('F');
    }

    public void switchG(View view) {
        switchG();
        updateTextViews('G');
    }

    public void switchH(View view) {
        switchH();
        updateTextViews('H');
    }

    public void switchI(View view) {
        switchI();
        updateTextViews('I');
    }

    public void switchJ(View view) {
        switchJ();
        updateTextViews('J');
    }

    public void changeNumber(int num) {
        String buttonId = "button"+num;
        int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
        Button btn = (Button)findViewById(resID);

        if (buttons[num] == false) {
            btn.setBackgroundResource(R.drawable.button_border_user2);
            btn.setTextColor(Color.WHITE);
            buttons[num] = true;
        } else {
            btn.setBackgroundResource(R.drawable.button_border_user);
            btn.setTextColor(Color.BLACK);
            buttons[num] = false;
        }
    }

    /**
     *  Regular grid color changes without button animation
     */

    public void switchA() {
        changeNumber(0);
        changeNumber(1);
        changeNumber(2);

        if (randomSwitches == false) {
            Button btnA = (Button)findViewById(R.id.switchA);
            if (switches[0] == false) {
                btnA.setBackgroundResource(R.drawable.letapressed);
                switches[0] = true;
            } else {
                btnA.setBackgroundResource(R.drawable.leta);
                switches[0] = false;
            }
        }
    }

    public void switchB() {
        changeNumber(3);
        changeNumber(7);
        changeNumber(9);
        changeNumber(11);

        if (randomSwitches == false) {
            Button btnB = (Button)findViewById(R.id.switchB);

            if (switches[1] == false) {
                btnB.setBackgroundResource(R.drawable.letbpressed);
                switches[1] = true;
            } else {
                btnB.setBackgroundResource(R.drawable.letb);
                switches[1] = false;
            }
        }
    }

    public void switchC() {
        changeNumber(4);
        changeNumber(10);
        changeNumber(14);
        changeNumber(15);

        if (randomSwitches == false) {
            Button btnC = (Button)findViewById(R.id.switchC);

            if (switches[2] == false) {
                btnC.setBackgroundResource(R.drawable.letcpressed);
                switches[2] = true;
            } else {
                btnC.setBackgroundResource(R.drawable.letc);
                switches[2] = false;
            }
        }
    }

    public void switchD() {
        changeNumber(0);
        changeNumber(4);
        changeNumber(5);
        changeNumber(6);
        changeNumber(7);

        if (randomSwitches == false) {
            Button btnD = (Button)findViewById(R.id.switchD);

            if (switches[3] == false) {
                btnD.setBackgroundResource(R.drawable.letdpressed);
                switches[3] = true;
            } else {
                btnD.setBackgroundResource(R.drawable.letd);
                switches[3] = false;
            }
        }
    }

    public void switchE() {
        changeNumber(6);
        changeNumber(7);
        changeNumber(8);
        changeNumber(10);
        changeNumber(12);

        if (randomSwitches == false) {
            Button btnE = (Button)findViewById(R.id.switchE);

            if (switches[4] == false) {
                btnE.setBackgroundResource(R.drawable.letepressed);
                switches[4] = true;
            } else {
                btnE.setBackgroundResource(R.drawable.lete);
                switches[4] = false;
            }
        }
    }

    public void switchF() {
        changeNumber(0);
        changeNumber(2);
        changeNumber(14);
        changeNumber(15);

        if (randomSwitches == false) {
            Button btnF = (Button)findViewById(R.id.switchF);

            if (switches[5] == false) {
                btnF.setBackgroundResource(R.drawable.letfpressed);
                switches[5] = true;
            } else {
                btnF.setBackgroundResource(R.drawable.letf);
                switches[5] = false;
            }
        }
    }

    public void switchG() {
        changeNumber(3);
        changeNumber(14);
        changeNumber(15);

        if (randomSwitches == false) {
            Button btnG = (Button)findViewById(R.id.switchG);

            if (switches[6] == false) {
                btnG.setBackgroundResource(R.drawable.letgpressed);
                switches[6] = true;
            } else {
                btnG.setBackgroundResource(R.drawable.letg);
                switches[6] = false;
            }
        }
    }

    public void switchH() {
        changeNumber(4);
        changeNumber(5);
        changeNumber(7);
        changeNumber(14);
        changeNumber(15);

        if (randomSwitches == false) {
            Button btnH = (Button)findViewById(R.id.switchH);

            if (switches[7] == false) {
                btnH.setBackgroundResource(R.drawable.lethpressed);
                switches[7] = true;
            } else {
                btnH.setBackgroundResource(R.drawable.leth);
                switches[7] = false;
            }
        }
    }

    public void switchI() {
        changeNumber(1);
        changeNumber(2);
        changeNumber(3);
        changeNumber(4);
        changeNumber(5);

        if (randomSwitches == false) {
            Button btnI = (Button)findViewById(R.id.switchI);

            if (switches[8] == false) {
                btnI.setBackgroundResource(R.drawable.letipressed);
                switches[8] = true;
            } else {
                btnI.setBackgroundResource(R.drawable.leti);
                switches[8] = false;
            }
        }
    }

    public void switchJ() {
        changeNumber(3);
        changeNumber(4);
        changeNumber(5);
        changeNumber(9);
        changeNumber(13);

        if (randomSwitches == false) {
            Button btnJ = (Button)findViewById(R.id.switchJ);

            if (switches[9] == false) {
                btnJ.setBackgroundResource(R.drawable.letjpressed);
                switches[9] = true;
            } else {
                btnJ.setBackgroundResource(R.drawable.letj);
                switches[9] = false;
            }
        }
    }

}
