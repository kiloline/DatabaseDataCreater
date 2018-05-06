package Utils.datastruc;

import CreateSQLParser.Lex.Word;
import CreateSQLParser.Lex.Word_Segment;
import CreateSQLParser.Parser.Fugue;
import CreateSQLParser.Parser.Parser;
import dataStruture.TableStructure;

import java.util.List;

public class CreateTableStruc {
    public static TableStructure makeStructure(String createSQL) throws Exception {
        Word_Segment ws = new Word_Segment();
        Fugue fugue = new Fugue();
        Parser parser = new Parser();

        List<Word> words = ws.Segment(createSQL);
        words = fugue.fugue(words);
        return parser.makeStrusture(words);
    }
}
