package tests;
import code.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TypecheckerTest {
    public static Map<Variable, Type> makeEmptyGamma() {
        return makeGamma(new String[0], new Type[0]);
    }
    
    public static Map<Variable, Type> makeGamma(final String[] variables,
                                                final Type[] types) {
        assert(variables.length == types.length);
        final Map<Variable, Type> gamma = new HashMap<Variable, Type>();
        
        for (int index = 0; index < variables.length; index++) {
            gamma.put(new Variable(variables[index]), types[index]);
        }

        return gamma;
    } // makeGamma

    public static List<FormalParameter> makeFormalParams(final Type[] types,
                                                         final String[] variables) {
        assert(types.length == variables.length);
        final List<FormalParameter> list = new ArrayList<FormalParameter>();

        for (int index = 0; index < types.length; index++) {
            list.add(new FormalParameter(types[index], new Variable(variables[index])));
        }

        return list;
    } // makeFormalParams

    public static List<Stmt> makeStatements(final Stmt... statements) {
        final List<Stmt> list = new ArrayList<Stmt>();

        for (final Stmt stmt : statements) {
            list.add(stmt);
        }

        return list;
    } // makeStatements
    
    public static Program makeProgram(final FirstOrderFunctionDefinition... fdef) {
        final List<FirstOrderFunctionDefinition> list = new ArrayList<FirstOrderFunctionDefinition>();
        for (final FirstOrderFunctionDefinition function : fdef) {
            list.add(function);
        }
        return new Program(list, null);
    } // makeProgram

    public static List<Exp> makeActualParams(final Exp... exps) {
        final List<Exp> list = new ArrayList<Exp>();
        for (final Exp exp : exps) {
            list.add(exp);
        }
        return list;
    } // makeActualParams
    
    public static Type typeof(final Map<Variable, Type> gamma,
                              final Exp e)
        throws IllTypedException {
        return (new Typechecker(makeProgram())).typeof(gamma, e);
    } // typeof

    public static Map<Variable, Type> statementGamma(final Stmt... stmts) throws IllTypedException {
        return new Typechecker(makeProgram()).typecheckStmts(makeEmptyGamma(),
                                                             false,
                                                             makeStatements(stmts));
    } // statementGamma
    
    @Test
    public void canAccessIntVariableInScope() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new VariableExp(new Variable("x"))));
    }

    @Test
    public void canAccessBoolVariableInScope() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new BoolType() }),
                            new VariableExp(new Variable("x"))));
    }
    
    @Test
    public void canAccessVarVariableInScope() throws IllTypedException {
        assertEquals(new VarType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new VarType() }),
                            new VariableExp(new Variable("x"))));
    }
    
    @Test
    public void canPrintVariableInScope() throws IllTypedException {
        assertEquals(new VarType(),
                     typeof(makeEmptyGamma(), new PrintExp(new VariableExp(new Variable("x")))));
    }
    
    @Test
    public void canPrintIntInScope() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(), new PrintExp(new IntegerExp(1))));
    }
    
    @Test
    public void canPrintBoolInScope() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(), new PrintExp(new BooleanExp(true))));
    }

    @Test(expected = IllTypedException.class)
    public void accessingOutOfScopeVariableIsIllTyped() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new VariableExp(new Variable("x")));
    }

    @Test
    public void integerExpReturnsInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new IntegerExp(0)));
    }

    @Test
    public void booleanExpReturnsBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BooleanExp(true)));
    }
    
    @Test
    public void intLessThanIntGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new LessThanBOP(),
                                         new IntegerExp(1))));
    }
    
    @Test
    public void intGreaterThanIntGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new GreaterThanBOP(),
                                         new IntegerExp(1))));
    }

    @Test(expected = IllTypedException.class)
    public void intLessThanBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new LessThanBOP(),
                            new BooleanExp(true)));
    }
    
    @Test(expected = IllTypedException.class)
    public void varLessThanVarGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new VariableExp(new Variable("foo")),
                            new LessThanBOP(),
                            new VariableExp(new Variable("foo"))));
    }

    @Test(expected = IllTypedException.class)
    public void boolLessThanIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new LessThanBOP(),
                            new IntegerExp(0)));
    }

    @Test(expected = IllTypedException.class)
    public void boolLessThanBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new LessThanBOP(),
                            new BooleanExp(false)));
    }

    @Test
    public void intEqualToIntGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(1),
                                         new EqualsToBOP(),
                                         new IntegerExp(1))));
    }
    
    @Test
    public void varEqualToVarGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new VariableExp(new Variable("foo")),
                                         new EqualsToBOP(),
                                         new VariableExp(new Variable("foo")))));
    }
    
    @Test
    public void boolEqualToBoolGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new BooleanExp(true),
                                         new EqualsToBOP(),
                                         new BooleanExp(true))));
    }


    @Test
    public void intPlusIntGivesInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new PlusBOP(),
                                         new IntegerExp(1))));
    }

    @Test
    public void intSubIntGivesInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new SubBOP(),
                                         new IntegerExp(1))));
    }
    @Test
    public void intMultIntGivesInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new MultBOP(),
                                         new IntegerExp(1))));
    }
    @Test
    public void intDivIntGivesInt() throws IllTypedException {
        assertEquals(new IntType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new IntegerExp(0),
                                         new DivBOP(),
                                         new IntegerExp(1))));
    }
    @Test
    public void varAddVarGivesVar() throws IllTypedException {
        assertEquals(new VarType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new VariableExp(new Variable("foo")),
                                         new PlusBOP(),
                                         new VariableExp(new Variable("foo")))));
    }
    @Test(expected = IllTypedException.class)
    public void intPlusBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new PlusBOP(),
                            new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void boolPlusIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new PlusBOP(),
                            new IntegerExp(0)));
    }

    @Test(expected = IllTypedException.class)
    public void boolPlusBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new PlusBOP(),
                            new BooleanExp(false)));
    }
    
    @Test
    public void boolAndBoolGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new BooleanExp(true),
                                         new AndBOP(),
                                         new BooleanExp(false))));
    }

    @Test
    public void boolOrBoolGivesBool() throws IllTypedException {
        assertEquals(new BoolType(),
                     typeof(makeEmptyGamma(),
                            new BinopExp(new BooleanExp(true),
                                         new OrBOP(),
                                         new BooleanExp(false))));
    } 
    
    @Test(expected = IllTypedException.class)
    public void boolAndIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new BooleanExp(true),
                            new AndBOP(),
                            new IntegerExp(0)));
    }

    @Test(expected = IllTypedException.class)
    public void intAndBoolGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new AndBOP(),
                            new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void intAndIntGivesTypeError() throws IllTypedException {
        typeof(makeEmptyGamma(),
               new BinopExp(new IntegerExp(0),
                            new AndBOP(),
                            new IntegerExp(0)));
    }
    /*@Test
    public void instanceDec() throws IllTypedException {
        assertEquals(new InstanceDec(new PublicToken(), new IntType(), new VariableExp(new Variable("foo")) ),
                     new InstanceDec(new PublicToken(),typeof(makeEmptyGamma() new I new VariableExp(new Variable("foo"))));
    }*/
    @Test
    public void canCreateHigherOrderFunction() throws IllTypedException {
        // (x: int) => true
        assertEquals(new FunctionType(new IntType(), new BoolType()),
                     typeof(makeEmptyGamma(),
                            new HigherOrderFunctionDef(new Variable("x"),
                                                       new IntType(),
                                                       new BooleanExp(true))));
    }

    @Test
    public void higherOrderFunctionCanUsePassedVariable() throws IllTypedException {
        // (x: int) => x + 1
        final Variable x = new Variable("x");
        assertEquals(new FunctionType(new IntType(), new IntType()),
                     typeof(makeEmptyGamma(),
                            new HigherOrderFunctionDef(x,
                                                       new IntType(),
                                                       new BinopExp(new VariableExp(x),
                                                                    new PlusBOP(),
                                                                    new IntegerExp(1)))));
    }

    @Test
    public void higherOrderFunctionCanCaptureEnvironment() throws IllTypedException {
        // [x -> int] (y: int) => y + x
        final Variable x = new Variable("x");
        final Variable y = new Variable("y");

        assertEquals(new FunctionType(new IntType(), new IntType()),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new HigherOrderFunctionDef(y,
                                                       new IntType(),
                                                       new BinopExp(new VariableExp(y),
                                                                    new PlusBOP(),
                                                                    new VariableExp(x)))));
    }

    @Test
    public void higherOrderFunctionCanShadowEnvironment() throws IllTypedException {
        // [x -> int] (x: bool) => x
        final Variable x = new Variable("x");
        
        assertEquals(new FunctionType(new BoolType(), new BoolType()),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                            new HigherOrderFunctionDef(x,
                                                       new BoolType(),
                                                       new VariableExp(x))));
    }

    @Test
    public void higherOrderFunctionsCanBeCalled() throws IllTypedException {
        // [x -> int => bool] x(3)
        final FunctionType ft = new FunctionType(new IntType(),
                                                 new BoolType());
        final Variable x = new Variable("x");
        
        assertEquals(new BoolType(),
                     typeof(makeGamma(new String[]{ "x" }, new Type[]{ ft }),
                            new CallHigherOrderFunction(new VariableExp(x),
                                                        new IntegerExp(0))));
    }

    @Test(expected = IllTypedException.class)
    public void higherOrderFunctionsNeedCorrectType() throws IllTypedException {
        // [x -> int => bool] x(true)
        final FunctionType ft = new FunctionType(new IntType(),
                                                 new BoolType());
        final Variable x = new Variable("x");

        typeof(makeGamma(new String[]{ "x" }, new Type[]{ ft }),
               new CallHigherOrderFunction(new VariableExp(x),
                                           new BooleanExp(true)));
    }

    @Test(expected = IllTypedException.class)
    public void cannotCallNonHigherOrderFunction() throws IllTypedException {
        // [x -> int] x(true)
        final Variable x = new Variable("x");

        typeof(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
               new CallHigherOrderFunction(new VariableExp(x),
                                           new BooleanExp(true)));
    }

    @Test
    public void emptyStmtDoesNothing() throws IllTypedException {
        assertEquals(makeEmptyGamma(),
                     statementGamma(new EmptyStmt()));
    }
    
    @Test(expected = IllTypedException.class)
    public void continueIsErrorIfNotInLoop() throws IllTypedException {
        statementGamma(new ContinueStmt());
    }

    @Test
    public void continueIsOkInLoop() throws IllTypedException {
        assertEquals(makeEmptyGamma(),
                     statementGamma(new ForStmt(new EmptyStmt(),
                                                new BooleanExp(true),
                                                new EmptyStmt(),
                                                makeStatements(new ContinueStmt()))));
    }
    
    @Test(expected = IllTypedException.class)
    public void breakIsErrorIfNotInLoop() throws IllTypedException {
        statementGamma(new BreakStmt());
    }

    @Test
    public void breakIsOkInLoop() throws IllTypedException {
        assertEquals(makeEmptyGamma(),
                     statementGamma(new ForStmt(new EmptyStmt(),
                                                new BooleanExp(true),
                                                new EmptyStmt(),
                                                makeStatements(new BreakStmt()))));
    }
    
    @Test
    public void breakIsOkInWLoop() throws IllTypedException {
        assertEquals(makeEmptyGamma(),
                     statementGamma(new WhileStmt(new BooleanExp(true),
                                                makeStatements(new BreakStmt()))));
    }

    @Test
    public void letPutsVariableInScope() throws IllTypedException {
        // let x: int = 3
        assertEquals(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                     statementGamma(new LetStmt(new Variable("x"),
                                                new IntType(),
                                                new IntegerExp(3))));
    }

    @Test
    public void letShadowsLastBinding() throws IllTypedException {
        // let x: int = 3
        // let x: bool = true
        assertEquals(makeGamma(new String[]{ "x" }, new Type[]{ new BoolType() }),
                     statementGamma(new LetStmt(new Variable("x"),
                                                new IntType(),
                                                new IntegerExp(3)),
                                    new LetStmt(new Variable("x"),
                                                new BoolType(),
                                                new BooleanExp(true))));
    }

    @Test(expected = IllTypedException.class)
    public void letNeedsCorrectTypeAnnotation() throws IllTypedException {
        // let x: int = true
        statementGamma(new LetStmt(new Variable("x"),
                                   new IntType(),
                                   new BooleanExp(true)));
    }

    @Test
    public void assignmentWorks() throws IllTypedException {
        // let x: int = 3
        // x = 4
        assertEquals(makeGamma(new String[]{ "x" }, new Type[]{ new IntType() }),
                     statementGamma(new LetStmt(new Variable("x"),
                                                new IntType(),
                                                new IntegerExp(3)),
                                    new AssignStmt(new Variable("x"), new IntegerExp(4))));
    }

    @Test(expected = IllTypedException.class)
    public void assignmentNeedsVariableInScope() throws IllTypedException {
        // x = 4
        statementGamma(new AssignStmt(new Variable("x"), new IntegerExp(4)));
    }

    @Test(expected = IllTypedException.class)
    public void assignmentNeedsToBeOfSameType() throws IllTypedException {
        // let x: int = 3
        // x = true
        statementGamma(new LetStmt(new Variable("x"),
                                   new IntType(),
                                   new IntegerExp(3)),
                       new AssignStmt(new Variable("x"), new BooleanExp(true)));
    }

    @Test
    public void normalForTypechecks() throws IllTypedException {
        // for(let x: int = 0; x < 10; x = x + 1) {}
        final Variable x = new Variable("x");
        assertEquals(makeEmptyGamma(),
                     statementGamma(new ForStmt(new LetStmt(x,
                                                            new IntType(),
                                                            new IntegerExp(0)),
                                                new BinopExp(new VariableExp(x),
                                                             new LessThanBOP(),
                                                             new IntegerExp(10)),
                                                new AssignStmt(x,
                                                               new BinopExp(new VariableExp(x),
                                                                            new PlusBOP(),
                                                                            new IntegerExp(1))),
                                                makeStatements())));
    }

    @Test(expected = IllTypedException.class)
    public void conditionInForMustBeBoolean() throws IllTypedException {
        // for(let x: int = 0; 10; x = x + 1) {}
        final Variable x = new Variable("x");
        statementGamma(new ForStmt(new LetStmt(x,
                                               new IntType(),
                                               new IntegerExp(0)),
                                   new IntegerExp(10),
                                   new AssignStmt(x,
                                                  new BinopExp(new VariableExp(x),
                                                               new PlusBOP(),
                                                               new IntegerExp(1))),
                                   makeStatements()));
    }

    @Test
    public void initializerInScopeInForBody() throws IllTypedException {
        // for(let x: int = 0; x < 10; x = x + 1) {
        //   x = x + 1;
        // }
        final Variable x = new Variable("x");        
        final AssignStmt increment =
            new AssignStmt(x,
                           new BinopExp(new VariableExp(x),
                                        new PlusBOP(),
                                        new IntegerExp(1)));

        assertEquals(makeEmptyGamma(),
                     statementGamma(new ForStmt(new LetStmt(x,
                                                            new IntType(),
                                                            new IntegerExp(0)),
                                                new BinopExp(new VariableExp(x),
                                                             new LessThanBOP(),
                                                             new IntegerExp(10)),
                                                increment,
                                                makeStatements(increment))));
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsCannotHaveDuplicateFormalParameterNames() throws IllTypedException {
        // int foo(int x, int x) { return 1; }
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             new FunctionName("foo"),
                                             makeFormalParams(new Type[]{ new IntType(), new IntType() },
                                                              new String[]{ "x", "x" }),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsNeedDistinctNames() throws IllTypedException {
        // int foo() { return 1; }
        // int foo() { return 1; }
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             new FunctionName("foo"),
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        new Typechecker(p);
    }
    
    @Test
    public void firstOrderFunctionsCanBeCalled() throws IllTypedException {
        // int foo() { return 1; }
        // foo()

        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        final Typechecker typechecker = new Typechecker(p);
        typechecker.typecheckProgram(p);
        assertEquals(new IntType(),
                     typechecker.typeof(makeEmptyGamma(),
                                        new CallFirstOrderFunction(fn,
                                                                   makeActualParams())));
    }

    @Test
    public void firstOrderFunctionsCanUseParams() throws IllTypedException {
        // int foo(int x) { return x; }
        // foo(1)

        final FunctionName fn = new FunctionName("foo");
        final List<FormalParameter> formalParams =
            makeFormalParams(new Type[]{ new IntType() },
                             new String[]{ "x" });
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             formalParams,
                                             makeStatements(),
                                             new VariableExp(new Variable("x")));
        final Program p = makeProgram(fdef);
        final List<Exp> actualParams =
            makeActualParams(new IntegerExp(1));
        final Typechecker typechecker = new Typechecker(p);
        typechecker.typecheckProgram(p);
        assertEquals(new IntType(),
                     new Typechecker(p).typeof(makeEmptyGamma(),
                                               new CallFirstOrderFunction(fn,
                                                                          actualParams)));
    }

    @Test
    public void firstOrderFunctionsCanTakeParams() throws IllTypedException {
        // int foo(int x, bool y, int => bool z) { return 1; }
        // [a -> int, b -> bool, c -> int => bool] foo(a, b, c)
        final Type[] types = new Type[]{ new IntType(),
                                         new BoolType(),
                                         new FunctionType(new IntType(), new BoolType()) };
        final FunctionName fn = new FunctionName("foo");
        final List<FormalParameter> formalParams =
            makeFormalParams(types,
                             new String[]{ "x", "y", "z" });
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             formalParams,
                                             makeStatements(),
                                             new IntegerExp(1));

        final Program p = makeProgram(fdef);
        final Map<Variable, Type> gamma =
            makeGamma(new String[]{ "a", "b", "c" },
                      types);

        final List<Exp> actualParams =
            makeActualParams(new VariableExp(new Variable("a")),
                             new VariableExp(new Variable("b")),
                             new VariableExp(new Variable("c")));
                                             
        assertEquals(new IntType(),
                     new Typechecker(p).typeof(gamma,
                                               new CallFirstOrderFunction(fn,
                                                                          actualParams)));
    }

    @Test(expected = IllTypedException.class)
    public void cannotCallNonexistantFirstOrderFunction() throws IllTypedException {
        // foo(true);
        final FunctionName fn = new FunctionName("foo");
        final Program p = makeProgram();
        new Typechecker(p).typeof(makeEmptyGamma(),
                                  new CallFirstOrderFunction(fn,
                                                             makeActualParams(new BooleanExp(true))));
    }

    @Test(expected = IllTypedException.class)
    public void firstOrderFunctionsRejectBadParams() throws IllTypedException {
        // int foo(int x) { return x; }
        // foo(true);
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new IntegerExp(1));
        final Program p = makeProgram(fdef);
        new Typechecker(p).typeof(makeEmptyGamma(),
                                  new CallFirstOrderFunction(fn,
                                                             makeActualParams(new BooleanExp(true))));
    }

    @Test(expected = IllTypedException.class)
    public void typecheckingFailsIfTypeErrorIsInBodyOfFirstOrderFunction() throws IllTypedException {
        // int foo() { break; return 1; }
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(new BreakStmt()),
                                             new IntegerExp(1));

        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }

    @Test(expected = IllTypedException.class)
    public void typecheckingFailsOnReturnTypeMismatch() throws IllTypedException {
        // int foo() { return true; }
        final FunctionName fn = new FunctionName("foo");
        final FirstOrderFunctionDefinition fdef =
            new FirstOrderFunctionDefinition(new IntType(),
                                             fn,
                                             makeFormalParams(new Type[0], new String[0]),
                                             makeStatements(),
                                             new BooleanExp(true));

        final Program p = makeProgram(fdef);
        new Typechecker(p).typecheckProgram(p);
    }
} // TypecheckerTest
