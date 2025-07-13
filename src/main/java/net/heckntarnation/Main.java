package net.heckntarnation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main {

    public static boolean debug = false;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //Read file from argument
        File file = new File(args[0]);
        if(args.length > 1 && args[1].equalsIgnoreCase("-debug")){debug = true;}
        BufferedReader fr = new BufferedReader(new FileReader(file));
        List<String> lines = fr.lines().toList();
        ArrayList<TokenParamed> tokens = new ArrayList<TokenParamed>();
        //Parse and tokenize
        for(String line : lines){
            String[] splitLine = line.split(" ");
            TokenParamed paramed = new TokenParamed(tokenize(splitLine[0]));
            if(paramed.token == null){continue;}
            for(int i = 1; i < splitLine.length; i++) {
                paramed.setParam(i-1, splitLine[i]);
            }
            tokens.add(paramed);
        }
        //Run
        run(tokens);
    }

    public static Token tokenize(String opCode){
        switch(opCode){
            case "|": {
                break;
            }
            case "#": {
                return Token.GRID_SIZE;
            }
            case "?": {
                return Token.OUTPUT_MODE;
            }
            case "+": {
                return Token.ADD;
            }
            case "-": {
                return Token.SUB;
            }
            case "*": {
                return Token.MUL;
            }
            case "/": {
                return Token.DIV;
            }
            case "%": {
                return Token.MOD;
            }
            case "^": {
                return Token.MOVE_UP;
            }
            case "v": {
                return Token.MOVE_DOWN;
            }
            case "<": {
                return Token.MOVE_LEFT;
            }
            case ">": {
                return Token.MOVE_RIGHT;
            }
            case ".(": {
                return Token.X_COMP;
            }
            case ".)": {
                return Token.Y_COMP;
            }
            case ".()": {
                return Token.XY_COMP;
            }
            case "&<": {
                return Token.LOAD;
            }
            case "<&": {
                return Token.UNLOAD;
            }
            case "=&": {
                return Token.INPUT_LOAD;
            }
            case "@": {
                return Token.LABEL;
            }
            case "->": {
                return Token.JUMP;
            }
            case "->*": {
                return Token.JUMP_NOT_PUSH;
            }
            case "->=": {
                return Token.JUMP_EQUAL;
            }
            case "-><": {
                return Token.JUMP_LESS;
            }
            case "->.": {
                return Token.JUMP_COMP;
            }
            case "<->": {
                return Token.REVERSE;
            }
            case ";": {
                return Token.HALT;
            }
            case "<-": {
                return Token.RETURN;
            }
            case "-.": {
                return Token.SKIP;
            }
            case ".!": {
                return Token.COMP_UNSET;
            }
            case ".=": {
                return Token.REG_COMP;
            }
            case "~": {
                return Token.EXIT;
            }
            case "<&>": {
                return Token.FLIP_STACK;
            }
            case ".&": {
                return Token.COMP_STACK;
            }
            case "(": {
                return Token.SET_X;
            }
            case ")": {
                return Token.SET_Y;
            }
            case "()": {
                return Token.SET_XY;
            }
            case "`": {
                return Token.FILL;
            }
        }
        return null;
    }

    public static void run(ArrayList<TokenParamed> tokens) throws InterruptedException {
        short[][] grid = new short[1][1];
        boolean num_output = true;
        int x = 0; int y = 0;
        int token_index = 0;
        Stack<Short> register = new Stack<>();
        boolean comp_flag = false;
        Scanner scanner = new Scanner(System.in);
        Stack<Integer> execution_stack = new Stack<>();
        boolean run = true;
        boolean skip = false;
        HashMap<String, Integer> labels = new HashMap<String,Integer>();
        //Label parse
        for(int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).token == Token.LABEL){
                labels.put(tokens.get(i).params[0], i);
            }
        }
        while(run){
            TokenParamed token = tokens.get(token_index);
            if(skip){
                if(!(token.token == Token.COMP_UNSET || token.token == Token.LABEL)){
                    token_index++;
                    continue;
                }
                skip = comp_flag;
            }
            if(debug) {
                System.out.println("[" + x + "]" + "[" + y + "] " + token);
            }
            switch(token.token){
                case GRID_SIZE:{
                    grid = new short[Integer.parseInt(token.params[0])][Integer.parseInt(token.params[1])];
                    break;
                }
                case OUTPUT_MODE:{
                    num_output = token.params[0].equalsIgnoreCase("num");
                    break;
                }
                case ADD:{
                    short operand;
                    if(token.params.length > 0){operand = Short.parseShort(token.params[0]);}else{operand = register.pop();}
                    grid[y][x] += operand;
                    break;
                }
                case SUB:{
                    short operand;
                    if(token.params.length > 0){operand = Short.parseShort(token.params[0]);}else{operand = register.pop();}
                    grid[y][x] -= operand;
                    break;
                }
                case MUL:{
                    short operand;
                    if(token.params.length > 0){operand = Short.parseShort(token.params[0]);}else{operand = register.pop();}
                    grid[y][x] *= operand;
                    break;
                }
                case DIV:{
                    short operand;
                    if(token.params.length > 0){operand = Short.parseShort(token.params[0]);}else{operand = register.pop();}
                    grid[y][x] /= operand;
                    //THIS SHOULD BE AN ILLEGAL INSTRUCTION IN ASSEMBLY-LIKE LANGUAGES! DIVISION IN SINFUL!
                    break;
                }
                case MOD:{
                    short operand;
                    if(token.params.length > 0){operand = Short.parseShort(token.params[0]);}else{operand = register.pop();}
                    grid[y][x] %= operand;
                    //SINFUL!!!
                    break;
                }
                case LOAD:{
                    register.push(token.params[0] == null ? grid[y][x] : Short.parseShort(token.params[0]));
                    break;
                }
                case UNLOAD:{
                    grid[y][x] = register.pop();
                    break;
                }
                case INPUT_LOAD:{
                    register.push(Short.parseShort(scanner.nextLine()));
                    break;
                }
                case MOVE_LEFT:{
                    try{
                        x--;
                        short t = grid[y][x];
                    }catch(ArrayIndexOutOfBoundsException e){
                        x++;
                    }
                    break;
                }
                case MOVE_RIGHT:{
                    try{
                        x++;
                        short t = grid[y][x];
                    }catch(ArrayIndexOutOfBoundsException e){
                        x--;
                    }
                    break;
                }
                case MOVE_DOWN:{
                    try{
                        y++;
                        short t = grid[y][x];
                    }catch(ArrayIndexOutOfBoundsException e){
                        y--;
                    }
                    break;
                }
                case MOVE_UP:{
                    try{
                        y--;
                        short t = grid[y][x];
                    }catch(ArrayIndexOutOfBoundsException e){
                        y++;
                    }
                    break;
                }
                case SET_X:{
                    x = Short.parseShort(token.params[0]);
                    break;
                }
                case SET_Y:{
                    y = Short.parseShort(token.params[0]);
                    break;
                }
                case SET_XY:{
                    x = Short.parseShort(token.params[0]);
                    y = Short.parseShort(token.params[1]);
                    break;
                }
                case FILL:{
                    for(int j = 0; j < grid.length; j++){
                        for(int i = 0; i < grid[y].length; i++){
                            grid[j][i] = Short.parseShort(token.params[0]);
                        }
                    }
                }
                case X_COMP:{
                    if((x == Short.parseShort(token.params[0]))) {
                        comp_flag = true;
                    }
                    break;
                }
                case Y_COMP:{
                    if((y == Short.parseShort(token.params[1]))) {
                        comp_flag = true;
                    }
                    break;
                }
                case XY_COMP:{
                    if((x == Short.parseShort(token.params[0]) && y == Short.parseShort(token.params[1]))) {
                        comp_flag = true;
                    }
                    break;
                }
                case COMP_UNSET:{
                    comp_flag = false;
                    skip = false;
                    break;
                }
                case REG_COMP:{
                    if(register.peek() == grid[y][x]) {
                        comp_flag = true;
                    }
                    break;
                }
                case COMP_STACK:{
                    if(!register.empty()){
                        comp_flag = true;
                    }
                    break;
                }
                case SKIP:{
                    if(comp_flag) {
                        skip = true;
                    }
                    break;
                }
                case JUMP:{
                    execution_stack.push(token_index);
                    token_index = labels.get(token.params[0]);
                    break;
                }
                case JUMP_NOT_PUSH:{
                    token_index = labels.get(token.params[0]);
                    break;
                }
                case JUMP_EQUAL:{
                    if(Short.parseShort(token.params[1]) == grid[y][x]) {
                        if(token.params.length != 3 && token.params[2].equalsIgnoreCase("*")){execution_stack.push(token_index);}
                        token_index = labels.get(token.params[0]);
                    }
                    break;
                }
                case JUMP_LESS:{
                    if(grid[y][x] < Short.parseShort(token.params[1])) {
                        if(token.params.length != 3 && !token.params[2].equalsIgnoreCase("*")){execution_stack.push(token_index);}
                        token_index = labels.get(token.params[0]);
                    }
                    break;
                }
                case JUMP_COMP:{
                    if(comp_flag) {
                        if(token.params.length != 3 && !token.params[2].equalsIgnoreCase("*")){execution_stack.push(token_index);}
                        token_index = labels.get(token.params[0]);
                        comp_flag = false;
                    }
                    break;
                }
                case REVERSE:{
                    StringBuilder sb = new StringBuilder(grid[y][x] + "");
                    sb.reverse();
                    grid[y][x] = Short.parseShort(sb.toString());
                    break;
                }
                case FLIP_STACK:{
                    Queue<Short> temp = new LinkedList<>();
                    while(!register.empty()){
                        temp.add(register.pop());
                    }
                    while(!temp.isEmpty()){
                        register.push(temp.remove());
                    }
                }
                case RETURN:{
                    if(!execution_stack.empty()) {
                        token_index = execution_stack.pop();
                    }
                    break;
                }
                case HALT:{
                    long amount = Long.parseLong(token.params[0]);
                    if(amount % 2 == 0){output(grid, num_output);}
                    Thread.sleep(amount);
                    break;
                }
                case EXIT:{
                    run = false;
                    break;
                }
            }
            token_index++;
        }
        output(grid, num_output);
    }

    public static void output(short[][] grid, boolean num_output){
        for(int y = 0; y < grid.length; y++){
            for(int x = 0; x < grid[y].length; x++){
                System.out.print(num_output ? String.format("%03d", grid[y][x]) + " " : (char)grid[y][x]);
            }
            System.out.print("\n");
        }
    }

    public enum Token{
        GRID_SIZE, OUTPUT_MODE,
        ADD, SUB, MUL, DIV, MOD,
        LOAD, UNLOAD, INPUT_LOAD, FILL,
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, SET_X, SET_Y, SET_XY,
        X_COMP, Y_COMP, XY_COMP, REG_COMP, COMP_UNSET, COMP_STACK, SKIP,
        LABEL, JUMP, JUMP_NOT_PUSH, JUMP_EQUAL, JUMP_LESS, JUMP_COMP, RETURN,
        REVERSE, FLIP_STACK,
        HALT,
        EXIT,

        Token();
    }
}