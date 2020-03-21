import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

public class Parser {
	private final Token[] tokens;

	public Parser(final Token[] tokens) {
		this.tokens = tokens;
	}

	private class ParseResult<A> {
		public final A result;
		public final int nextPos;

		public ParseResult(final A result, final int nextPos) {
			this.result = result;
			this.nextPos = nextPos;
		}
	}

	private void checkTokenIs(final int position, final Token token) throws ParseException {
		if (!tokens[position].equals(token)) {
			throw new ParseException("Expected: " + token.toString() + "Received: " + tokens[position].toString(), position);
		}
	}

	public ParseResult<List<Exp>> parseAdditiveExpHelper(final int startPos) {
		final List<Exp> resultList = new ArrayList<Exp>();
		int curPos = startPos;

		while (curPos < tokens.length) {
			try {
				checkTokenIs(curPos, new AdditionToken());
				final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
				curPos = curPrimary.nextPos;
				resultList.add(curPrimary.result);
			} catch (final ParseException e) {
				try {
					checkTokenIs(curPos, new SubtractionToken());
					final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
					curPos = curPrimary.nextPos;
					resultList.add(curPrimary.result);
				} catch (final ParseException e1) {
					try {
						checkTokenIs(curPos, new MultiplicationToken());
						final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
						curPos = curPrimary.nextPos;
						resultList.add(curPrimary.result);
					} catch (final ParseException e2) {
						try {
							checkTokenIs(curPos, new DivisionToken());
							final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
							curPos = curPrimary.nextPos;
							resultList.add(curPrimary.result);
						} catch (final ParseException e3) {
							break;
						}
					}
				}
			}
		}
		return new ParseResult<List<Exp>>(resultList, curPos);
	}

	public ParseResult<Exp> parseAdditiveExp(final int startPos) throws ParseException {
		final ParseResult<Exp> starting = parsePrimary(startPos);
		final ParseResult<List<Exp>> rest = parseAdditiveExpHelper(starting.nextPos);
		Exp resultExp = starting.result;
		int pos = startPos + 1;
		for (final Exp otherExp : rest.result) {
			if(tokens[pos].equals(new AdditionToken())) {
			resultExp = new PlusExp(resultExp, otherExp);
		} else if(tokens[pos].equals(new SubtractionToken())){
			resultExp = new SubExp(resultExp, otherExp);
		} else if(tokens[pos].equals(new MultiplicationToken())){
			resultExp = new MultExp(resultExp, otherExp);
		} else {
			resultExp = new DivExp(resultExp, otherExp);
		}
			pos += 2;
		} 
		return new ParseResult<Exp>(resultExp, rest.nextPos);
	}

	public ParseResult<Exp> parsePrimary(final int startPos) throws ParseException {
		if (tokens[startPos] instanceof VariableToken) {
			final VariableToken asVar = (VariableToken) tokens[startPos];
			return new ParseResult<Exp>(new VariableExp(asVar.name), startPos + 1);
		} else if (tokens[startPos] instanceof IntegerToken) {
			final IntegerToken asInt = (IntegerToken) tokens[startPos];
			return new ParseResult<Exp>(new IntegerExp(asInt.value), startPos + 1);
		} else if (tokens[startPos] instanceof BooleanToken) {
			final BooleanToken asInt = (BooleanToken) tokens[startPos];
			return new ParseResult<Exp>(new BooleanExp(asInt.value), startPos + 1);
		} else if (tokens[startPos] instanceof IncrementToken) {
			final IntegerToken asInt = (IntegerToken) tokens[startPos + 1];
			return new ParseResult<Exp>(new IntegerExp(asInt.value + 1), startPos + 2);
		} else {
			checkTokenIs(startPos, new LeftParenToken());
			final ParseResult<Exp> inner = parseExp(startPos + 1);
			checkTokenIs(inner.nextPos, new RightParenToken());
			return new ParseResult<Exp>(inner.result, inner.nextPos + 1);
		}
	}

	public ParseResult<Exp> parseExp(final int startPos) throws ParseException {
		if (tokens[startPos] instanceof IfToken) {
			checkTokenIs(startPos + 1, new LeftParenToken());
			final ParseResult<Exp> guard = parseExp(startPos + 2);
			checkTokenIs(guard.nextPos, new RightParenToken());
			final ParseResult<Exp> ifTrue = parseExp(guard.nextPos + 1);
			checkTokenIs(ifTrue.nextPos, new ElseToken());
			final ParseResult<Exp> ifFalse = parseExp(ifTrue.nextPos + 1);
			return new ParseResult<Exp>(new IfExp(guard.result, ifTrue.result, ifFalse.result), ifFalse.nextPos);
		} else if (tokens[startPos] instanceof WhileToken) {
			checkTokenIs(startPos + 1, new LeftParenToken());
			final ParseResult<Exp> guard = parseExp(startPos + 2);
			checkTokenIs(guard.nextPos, new RightParenToken());
			final ParseResult<Exp> ifTrue = parseExp(guard.nextPos + 1);
			return new ParseResult<Exp>(new WhileExp(guard.result, ifTrue.result), ifTrue.nextPos);
		} else if (tokens[startPos] instanceof ReturnToken) {
			return parseAdditiveExp(startPos + 1);
		} else if (tokens[startPos] instanceof ForToken) {
			checkTokenIs(startPos + 1, new LeftParenToken());
			final ParseResult<Exp> init = parseExp(startPos + 2);
			final ParseResult<Exp> cond = parseExp(startPos + 3);
			final ParseResult<Exp> inc = parseExp(startPos + 4);
			checkTokenIs(inc.nextPos, new RightParenToken());
			final ParseResult<Exp> exec = parseExp(inc.nextPos + 1);
			return new ParseResult<Exp>(new ForExp(init.result, cond.result, inc.result, exec.result), exec.nextPos);
		} else if (tokens[startPos] instanceof ClassToken) {
				if(tokens[startPos + 2].equals(new ExtendsToken())) {
			checkTokenIs(startPos + 2, new ExtendsToken());
			final ParseResult<Exp> nclass = parseExp(startPos+1);
			final ParseResult<Exp> sclass = parseExp(startPos + 4);
			return new ParseResult<Exp>(new ExtendExp(nclass.result, sclass.result), sclass.nextPos);
				}
				return parseAdditiveExp(startPos);
		} else {
			return parseAdditiveExp(startPos);
		}
	}

	public Exp parseToplevelExp() throws ParseException {
		final ParseResult<Exp> result = parseExp(0);

		if (result.nextPos == tokens.length) {
			return result.result;
		} else {
			throw new ParseException("extra tokens at end", result.nextPos - 1);
		}
	}
}
