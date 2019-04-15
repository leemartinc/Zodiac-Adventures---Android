package com.leemartinc.zodiacadventures;

import android.content.Context;
//system api
import android.content.SharedPreferences;
//systemapi
import android.graphics.Typeface;

//system call
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leemartinc.zodiacadventures.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //system call
    Handler mHandler = new Handler();


    SharedPreferences userInfo;
    SharedPreferences.Editor userInfoEditor;
    ImageButton button_up;
    ImageButton button_down;
    ImageButton button_left;
    ImageButton button_right;
    TextView game_screen;
    TextView position;

    String playerClass;
    String playerName;
    TextView playerName_view;
    TextView playerArmor_view;
    TextView playerHealth_view;
    TextView playerPower_view;

    TextView playerSkill1_view;
    TextView playerSkill2_view;

    TextView mon_name_view;
    TextView mon_health_view;
    Button use_skill_1;
    Button use_skill_2;

    //game logic stuff
    Map<String, String> STUFF;
    HashMap<String, int[]> SPECIAL_POS;
    char character;
    char[][] room;
    int room_height;
    int room_width;
    int pos[] = {0,0};
    boolean combatMode = false;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    boolean gameWin = false;


    //monster stuff
    char mon_char;
    String mon_name = "no mon";
    int mon_health = 0;
    int mon_def;
    int mon_attack;

    //combat stuff
    int attackDamage;
    int turn;
    int playerCombatHP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        STUFF = new HashMap<String, String>();
        SPECIAL_POS = new HashMap<String, int[]>();

        //system calls
        userInfo = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
        userInfoEditor = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).edit();

        playerClass = userInfo.getString("CLASS", "no class");
        playerName = userInfo.getString("CHAR_NAME", "no name");
        playerCombatHP = userInfo.getInt("HEALTH", 0);

        SPECIAL_POS.put("mon1", new int[] {20,18});
        SPECIAL_POS.put("mon2", new int[] {15,11});
        SPECIAL_POS.put("mon3", new int[] {8,3});
        SPECIAL_POS.put("mon4", new int[] {13,28});
        SPECIAL_POS.put("mon5", new int[] {21,25});
        SPECIAL_POS.put("mon6", new int[] {1,3});
        SPECIAL_POS.put("mon7", new int[] {1,13});
        SPECIAL_POS.put("monw", new int[] {4,39});
        SPECIAL_POS.put("win", new int[] {21,38});


        STUFF.put("empty", "·");
        STUFF.put("wall_x", "=");
        STUFF.put("wall_y", "|");
        if(playerName == "no name"){
            STUFF.put("player", "0");
        }else {
            STUFF.put("player", playerName);
        }

           /*
                userInfo.putString("CHAR_NAME", name.getText().toString());
                //userInfo.putString("CHAR_ICON", name.getText().toString().charAt(0));
                userInfo.putInt("DEFENSE", player_def);
                userInfo.putInt("SKILL1", player_skill1);
                userInfo.putInt("SKILL2", player_skill2);
                userInfo.putInt("HEALTH", player_health);
                userInfo.putString("CLASS", gameClass);
         */


       //Toast.makeText(getApplicationContext(), playerClass, Toast.LENGTH_SHORT).show();
        game_screen = findViewById(R.id.viewFinder);
        position = findViewById(R.id.location);

        playerName_view = findViewById(R.id.playerAlias);
        playerArmor_view = findViewById(R.id.player_armor);
        playerHealth_view = findViewById(R.id.player_health);
        playerPower_view = findViewById(R.id.player_power);

        playerSkill1_view = findViewById(R.id.skill1);
        playerSkill2_view = findViewById(R.id.skill2);

        mon_name_view = findViewById(R.id.mon_name);
        mon_health_view = findViewById(R.id.mon_health);

        use_skill_1 = findViewById(R.id.skill_1);
        use_skill_2 = findViewById(R.id.skill_2);

        scene_builder("map_world.txt");
        updater();


        button_up = findViewById(R.id.btnup);
        button_down = findViewById(R.id.btndown);
        button_left = findViewById(R.id.btnleft);
        button_right= findViewById(R.id.btnright);


        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up(room,STUFF.get("empty").toCharArray()[0],STUFF.get("player").toCharArray()[0]);
                updater();
            }
        });

        button_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down(room,STUFF.get("empty").toCharArray()[0],STUFF.get("player").toCharArray()[0]);
                updater();
            }
        });
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left(room,STUFF.get("empty").toCharArray()[0],STUFF.get("player").toCharArray()[0]);
                updater();
            }
        });
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right(room,STUFF.get("empty").toCharArray()[0],STUFF.get("player").toCharArray()[0]);
                updater();
            }
        });

        //Toast.makeText(getApplicationContext(), "not waiting for buttons", Toast.LENGTH_SHORT).show();

        //==================================================================================================

        use_skill_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fightClub(userInfo.getInt("SKILL1", 0));
            }
        });
        use_skill_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.longBitsToDouble(userInfo.getLong("POWER", 0)) > 2.0) {
                    fightClub(userInfo.getInt("SKILL2", 0));
                    double newPower = Double.longBitsToDouble(userInfo.getLong("POWER", 0)) - (Double.longBitsToDouble(userInfo.getLong("POWER", 0))*.3) ;
                    userInfoEditor.putLong("POWER", Double.doubleToRawLongBits(newPower));
                    userInfoEditor.apply();
                    playerPower_view.setText("Power: " + String.valueOf(df2.format(Double.longBitsToDouble(userInfo.getLong("POWER", 0)))));

                }else{
                    Toast.makeText(getApplicationContext(), "You don't have enough power. You are too weak!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updater(){

        playerName_view.setText(playerName + " the " + userInfo.getString("CLASS", "null"));
        playerArmor_view.setText("Armor: " + String.valueOf(userInfo.getInt("DEFENSE", 00)));
        playerHealth_view.setText("Health: " + String.valueOf(userInfo.getInt("HEALTH", 00)));
        playerPower_view.setText("Power: " + String.valueOf(df2.format(Double.longBitsToDouble(userInfo.getLong("POWER", 0)))));

        playerSkill1_view.setText("Skill 1 max dmg: " + String.valueOf(userInfo.getInt("SKILL1", 00)));
        playerSkill2_view.setText("Skill 2 max dmg: " + String.valueOf(userInfo.getInt("SKILL2", 00)));

        print_game();
        player_pos();
        checkSpecialPos();

        if(combatMode){

            //fightClub();
        }

    }

    public void print_game(){
        //print map to text view
        String toPrint = "";

        for (int i = 0; i < room_height; i++)
        {
            for (int j = 0; j < room_width; j++)
            {
                if (room[i][j] == '0') {
                    pos[0] = i;
                    pos[1] = j;
                    room[i][j] = STUFF.get("player").toCharArray()[0];
                }
                toPrint += room[i][j];
            }
        }

        game_screen.setText(toPrint);
    }

    //player position X,Y
    public void player_pos(){
        for (int i = 0; i < room_height; i++)
        {
            for (int j = 0; j < room_width; j++)
            {
                if (room[i][j] == character) {
                    pos[0] = i;
                    pos[1] = j;
                }
            }
        }

        position.setText(Arrays.toString(pos));
    }

    public void scene_builder(String file){
        //import game file
        //using system call
        String content = "";
        character = STUFF.get("player").toCharArray()[0];
        try{
            //system call
            InputStream is = getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            //system call
            is.read(buffer);
            //system call
            is.close();

            content = new String(buffer);
        }catch(IOException e){
            e.printStackTrace();
        }

        room_height = 0;
        int init_col_counter= 0;
        room_width = 0;


        for (char ch: content.toCharArray()) {
            init_col_counter++;

            if(ch == '\n'){
                room_height++;
                room_width = init_col_counter;
                init_col_counter = 0;
            }

        }


        room = new char[room_height+1][room_width];

        int line_counter = 0;
        int col_counter= 0;

        for (char ch: content.toCharArray()) {
            if (ch == '0'){
                ch = character;
            }
            if(ch == '*'){
                ch = mon_char;
            }
            if (ch == 'Ж'){
                if(gameWin){
                    ch = '·';
                }
            }

            room[line_counter][col_counter] = ch;
            col_counter++;

            if(ch == '\n'){
                line_counter++;
                col_counter = 0;
            }
        }
            //System.out.println("Line counter: " + line_counter);

            //System.out.println(Arrays.deepToString(room));
            game_screen.setText(content);
            game_screen.setTypeface(Typeface.MONOSPACE);
    }

    public void up(char[][] map, char inst_replace, char inst_player){

        //system api graphic
        if(map[pos[0] - 1][pos[1]] != STUFF.get("wall_x").toCharArray()[0] && map[pos[0] - 1][pos[1]] != STUFF.get("wall_y").toCharArray()[0]) {
            map[pos[0]][pos[1]] = inst_replace;
            map[pos[0] - 1][pos[1]] = inst_player;
        }
        player_pos();
    }

    public void down(char[][] map, char inst_replace, char inst_player){
        //system api graphic
        if(map[pos[0] + 1][pos[1]] != STUFF.get("wall_x").toCharArray()[0] && map[pos[0] + 1][pos[1]] != STUFF.get("wall_y").toCharArray()[0]) {
            map[pos[0]][pos[1]] = inst_replace;
            map[pos[0] + 1][pos[1]] = inst_player;
        }
        player_pos();
    }
    public void left(char[][] map, char inst_replace, char inst_player){
        //system api graphic
        if(map[pos[0]][pos[1] - 1] != STUFF.get("wall_x").toCharArray()[0] && map[pos[0]][pos[1] - 1] != STUFF.get("wall_y").toCharArray()[0]) {
            map[pos[0]][pos[1]] = inst_replace;
            map[pos[0]][pos[1] - 1] = inst_player;
        }
        player_pos();
    }
    public void right(char[][] map, char inst_replace, char inst_player){
        //system api graphic
        if(map[pos[0]][pos[1] + 1] != STUFF.get("wall_x").toCharArray()[0] && map[pos[0]][pos[1] + 1] != STUFF.get("wall_y").toCharArray()[0]) {
            map[pos[0]][pos[1]] = inst_replace;
            map[pos[0]][pos[1] + 1] = inst_player;
        }
        player_pos();
    }

    public void monster_stats(){

        mon_name_view.setText(mon_name + " the monster");
        mon_health_view.setText("Monster health: " + String.valueOf(mon_health));

        mon_name_view.setVisibility(View.VISIBLE);
        mon_health_view.setVisibility(View.VISIBLE);
        use_skill_1.setVisibility(View.VISIBLE);
        use_skill_2.setVisibility(View.VISIBLE);

    }

    public void checkSpecialPos(){
        if (Arrays.equals(pos, SPECIAL_POS.get("mon1"))){
            mon_char = '1';
            mon_name = "Wan";
            mon_health = 60;
            mon_def = 20;
            mon_attack = 20;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }
        if (Arrays.equals(pos, SPECIAL_POS.get("mon2"))){
            mon_char = '2';
            mon_name = "Too";
            mon_health = 400;
            mon_def = 30;
            mon_attack = 88;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }
        if (Arrays.equals(pos, SPECIAL_POS.get("mon3"))){
            mon_char = '3';
            mon_name = "Tree";
            mon_health = 800;
            mon_def = 40;
            mon_attack = 430;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }if (Arrays.equals(pos, SPECIAL_POS.get("mon4"))){
            mon_char = '4';
            mon_name = "Fur";
            mon_health = 1200;
            mon_def = 55;
            mon_attack = 777;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }if (Arrays.equals(pos, SPECIAL_POS.get("mon5"))){
            mon_char = '5';
            mon_name = "Fiff";
            mon_health = 2600;
            mon_def = 70;
            mon_attack = 1000;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }if (Arrays.equals(pos, SPECIAL_POS.get("mon6"))){
            mon_char = '6';
            mon_name = "Sees";
            mon_health = 4000;
            mon_def = 80;
            mon_attack = 1800;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }if (Arrays.equals(pos, SPECIAL_POS.get("mon7"))){
            mon_char = '7';
            mon_name = "Steven";
            mon_health = 10000;
            mon_def = 100;
            mon_attack = 3000;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }if (Arrays.equals(pos, SPECIAL_POS.get("monw"))){
            mon_char = 'Ж';
            mon_name = "Wise Dragon";
            mon_health = 2000000;
            mon_def = 200;
            mon_attack = 7000;
            //gameCombat(specialStuff['mon1'])
            scene_builder("map_combat.txt");
            print_game();
            monster_stats();
            combatMode = true;
            playerCombatHP = userInfo.getInt("HEALTH", 0);
            Toast.makeText(getApplicationContext(), "Fight!", Toast.LENGTH_SHORT).show();
            updater();
        }
        if (Arrays.equals(pos, SPECIAL_POS.get("win"))){
            scene_builder("win.txt");
            print_game();
        }







    }

    public void fightClub(int dmg){
        use_skill_1.setVisibility(View.INVISIBLE);
        use_skill_2.setVisibility(View.INVISIBLE);

        //Toast.makeText(getApplicationContext(), "Recognising button press", Toast.LENGTH_SHORT).show();
        Random rand = new Random();

        //randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
        double preAttack = (((dmg) * .61) + rand.nextInt((dmg) - (int) Math.round((dmg) * .61)) + 1);
        attackDamage = Integer.valueOf((int) Math.round(preAttack));

        if (attackDamage < 0) {
            attackDamage = 0;
            Toast.makeText(getApplicationContext(), "no damage to monster", Toast.LENGTH_SHORT).show();
        }
        mon_health -= attackDamage;
        mon_health_view.setText(String.valueOf("Monster Health:"+mon_health));
        Toast.makeText(getApplicationContext(), "You did " + attackDamage + " damage to monster", Toast.LENGTH_SHORT).show();

        try {
            wait(1000);
        } catch (Exception e) {}

        //==============mon attack

        //attackDamage = random.randint(int(m_atk-(m_atk*.4)),int(m_atk)) - p_def**(1/2)
        attackDamage = (((int) Math.round(mon_attack-(mon_attack*.4))) + rand.nextInt((int) Math.round(mon_attack) - (int) Math.round(mon_attack-(mon_attack*.4))) + 1) - (userInfo.getInt("DEFENSE", 00))^(1/2);

        if(attackDamage < 0){
            attackDamage = 0;
        }

        playerCombatHP -= attackDamage;
        playerHealth_view.setText(String.valueOf("Health: "+ playerCombatHP));
        Toast.makeText(getApplicationContext(), "Monster attacked you with " + attackDamage + " damage", Toast.LENGTH_SHORT).show();
        try {
            wait(1000);
        } catch (Exception e) {}

        if(playerCombatHP > 0 && mon_health > 0) {
            use_skill_1.setVisibility(View.VISIBLE);
            use_skill_2.setVisibility(View.VISIBLE);
        }else{
            if(playerCombatHP > 0){
                if(mon_name == "Wise Dragon"){
                    chickenDinner();
                }
                //you win
                Toast.makeText(getApplicationContext(), "you win", Toast.LENGTH_SHORT).show();

                //randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
                int prob = 5 + rand.nextInt((9-5)+1);

                //TODO how increase player defense
                //store as long
                double newPower = Double.longBitsToDouble(userInfo.getLong("POWER", 0)) + mon_def * 0.1;
                userInfoEditor.putLong("POWER", Double.doubleToRawLongBits(newPower));

                double newHealth = userInfo.getInt("HEALTH", 00) + ((2*newPower)*.8);
                userInfoEditor.putInt("HEALTH", (int) Math.round(newHealth));

                int newSk1 = (int)Math.round(userInfo.getInt("SKILL1", 0)+((2*newPower)*.6));
                userInfoEditor.putInt("SKILL1", newSk1);

                int newSk2 = (int)Math.round((userInfo.getInt("SKILL1", 0) + userInfo.getInt("SKILL1", 0)*(prob/10.0)));
                userInfoEditor.putInt("SKILL2", newSk2);

                int newDef = (int)Math.round(userInfo.getInt("DEFENSE", 0)+((2*newPower)*.6));
                userInfoEditor.putInt("DEFENSE", newDef);

                userInfoEditor.apply();
                Toast.makeText(getApplicationContext(), "Your stats have been improved", Toast.LENGTH_SHORT).show();

                updater();
                endCombat();

            }else{
                //you loose
                Toast.makeText(getApplicationContext(), "YOU LOSE! GAME OVER!", Toast.LENGTH_SHORT).show();
                //system call
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                        //system call
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }, 2000);

            }

        }
    }

    public void endCombat(){
        mon_name_view.setVisibility(View.INVISIBLE);
        mon_health_view.setVisibility(View.INVISIBLE);
        scene_builder("map_world.txt");
        combatMode = false;
        updater();
    }

    public void chickenDinner(){
        gameWin = true;
        SPECIAL_POS.put("monw", new int[] {0,0});
    }

}
