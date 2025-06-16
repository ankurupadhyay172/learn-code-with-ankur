import java.util.*;

enum PieceType{
    X,O
}

class PlayingPiece {
    PieceType pieceType;
    PlayingPiece(PieceType pieceType){
        this.pieceType = pieceType;
    }
}

class PlayingPieceX extends PlayingPiece{
    PlayingPieceX(){
        super(PieceType.X);
    }
}

class PlayingPieceO extends PlayingPiece{
    PlayingPieceO(){
        super(PieceType.O);
    }
}

class Player{
    private String name;
    private PlayingPiece playingPiece;
    boolean isComputer;
    Player(String name,PlayingPiece playingPiece,boolean isComputer){
        this.name = name;
        this.playingPiece = playingPiece;
        this.isComputer = isComputer;
    }
    String getName(){
        return name;
    }
    PlayingPiece getPlaingPiece(){
        return playingPiece;
    }
}

class Board{
    int size;
    PlayingPiece[][] board;

    Board(int size){
        this.size = size;
        board = new PlayingPiece[size][size];
    }

    void printBoard(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j]==null){
                    System.out.print("_ ");
                }else{
                    System.out.print(board[i][j].pieceType+" ");
                }
            }
             System.out.println();
        }
        System.out.println();
    }

    boolean addPlayingPiece(int row,int col,PlayingPiece playingPiece){
        if(board[row][col]!=null){
            System.out.println("Please enter the correct position");
            System.out.println();
            return false;
        }
        board[row][col]=playingPiece;
        return true;
    }
    
    void removePiece(int row,int col){
        board[row][col] = null;
    }
    
    List<Pair> getFreeCells(){
        List<Pair> freeCells = new ArrayList();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j]==null){
                    freeCells.add(new Pair(i,j));
                }
            }
        }
        return freeCells;
    }
}

class Pair{
    int first;
    int second;
    Pair(int first,int second){
        this.first = first;
        this.second = second;
    }
}

class TicTacToe{
    Deque<Player> players;
    Board board;
    TicTacToe(){
        initGame();
    }

    void initGame(){
        players = new LinkedList();
        board = new Board(3);
        players.add(new Player("Ankur",new PlayingPieceX(),false));
        players.add(new Player("Harshu",new PlayingPieceO(),true));
    }
    
    int minMax(boolean isMax,int row,int col,PieceType aiPiece,PieceType humen){
        if(isThereWinner(row,col,aiPiece)) return 10;
        if(isThereWinner(row,col,humen)) return -10;
        if(board.getFreeCells().isEmpty()) return 0;
        int best = isMax?Integer.MIN_VALUE:Integer.MAX_VALUE;
        for(Pair move:board.getFreeCells()){
            board.addPlayingPiece(move.first,move.second,isMax?new PlayingPiece(aiPiece):new PlayingPiece(humen));
            int score = minMax(!isMax,move.first,move.second,aiPiece,humen);
            board.removePiece(move.first,move.second);
            best = isMax?Math.max(score,best):Math.min(score,best);
        }
        return best;
    }
    
    Pair findBestMove(PieceType aiPiece,PieceType humen){
        Pair bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for(Pair move:board.getFreeCells()){
            board.addPlayingPiece(move.first,move.second,new PlayingPiece(aiPiece));
            int score = minMax(false,move.first,move.second,aiPiece,humen);
            board.removePiece(move.first,move.second);
            if(score>bestScore){
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    String startGame(){
        boolean isNoWinner = true;
        Scanner inputScanner = new Scanner(System.in);
        int row,col;
        while(isNoWinner){
            board.printBoard();
            Player playerTurn = players.removeFirst();
            List<Pair> freeCells = board.getFreeCells();
            if(freeCells.isEmpty()){
                isNoWinner = false;
                break;
            }
            if(playerTurn.isComputer){
                Pair bestMove = findBestMove(new PlayingPieceO().pieceType,new PlayingPieceX().pieceType);
                row = bestMove.first;
                col = bestMove.second;
            }else{
                 System.out.println(playerTurn.getPlaingPiece().pieceType+" - Enter the row and col like: 1,2");
            System.out.println();
            String input = inputScanner.nextLine();
            System.out.println();
            String[] values = input.split(",");
            row = Integer.parseInt(values[0].trim());
            col = Integer.parseInt(values[1].trim());     
            }
           
            boolean isSuccessfullyAdded = board.addPlayingPiece(row,col,playerTurn.getPlaingPiece());
            if(!isSuccessfullyAdded){
                players.addFirst(playerTurn);
                continue;
            }
            players.addLast(playerTurn);
            boolean isThereWinner = isThereWinner(row,col,playerTurn.getPlaingPiece().pieceType);
            if(isThereWinner){
                inputScanner.close();
                board.printBoard();
                return playerTurn.getName();
            }
        }
        inputScanner.close();
        return "Tie";
    }

    boolean isThereWinner(int row,int col,PieceType pieceType){
        boolean isRowMatch = true;
        boolean isColMatch = true;
        boolean isDiagonallyMatch = true;
        boolean isAntiDiagonallyMatch = true;

        for(int i=0;i<board.size;i++){
            if(board.board[row][i]==null||
            board.board[row][i].pieceType!=pieceType){
                isRowMatch = false;
            }
        }

        for(int i=0;i<board.size;i++){
            if(board.board[i][col]==null||
            board.board[i][col].pieceType!=pieceType){
                isColMatch = false;
            }
        }

        for(int i=0,j=0;i<board.size;i++,j++){
            if(board.board[i][j]==null||
            board.board[i][j].pieceType!=pieceType){
                isDiagonallyMatch = false;
            }
        }

        for(int i=0,j=board.size-1;i<board.size;i++,j--){
            if(board.board[i][j]==null||
            board.board[i][j].pieceType!=pieceType){
                isAntiDiagonallyMatch = false;
            }
        }

        return isRowMatch||isColMatch||isDiagonallyMatch||isAntiDiagonallyMatch;
    }
}


class LLDTicTacToeWithAi{
    public static void main(String[] args){
        TicTacToe game = new TicTacToe();
        String result = game.startGame();
        System.out.println("Game Over ! "+result);
    }
}