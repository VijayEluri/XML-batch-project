/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package net.sf.jsqlparser.expression;

import edu.bxml.generator.SqlAnalyzer;



/**
 * A basic class for binary expressions, that is expressions having a left member and a right member
 * which are in turn expressions. 
 */
public abstract class BinaryExpression implements Expression {
	private Expression leftExpression;
	private Expression rightExpression;
	private boolean not = false;

	public BinaryExpression() {
	}
	

	public Expression getLeftExpression() {
		return leftExpression;
	}

	public Expression getRightExpression() {
		return rightExpression;
	}

	public void setLeftExpression(Expression expression) {
		leftExpression = expression;
	}

	public void setRightExpression(Expression expression) {
		rightExpression = expression;
	}

	public void setNot() {
		not = true;
	}

	public boolean isNot() {
		return not;
	}

	public String toString() {
		Expression left = getLeftExpression();
		Expression right = getRightExpression();
		String strLeft = left.toString();
		String strRight = right.toString();
		boolean leftConst = false;
		boolean rightConst = false;
		SqlAnalyzer x;
		if (SqlAnalyzer.isVariable()) {
			if (left instanceof StringValue || left instanceof LongValue) {
				leftConst = true;
				strLeft = "#{" + SqlAnalyzer.camelCase(strRight) + ",jdbcType=" + SqlAnalyzer.getJdbcType(strLeft) + "}";
			}
			if (right instanceof StringValue  || right instanceof LongValue) {
				rightConst = true;
				strRight = "#{" + SqlAnalyzer.camelCase(strLeft) + ",jdbcType=" + SqlAnalyzer.getJdbcType(strLeft) + "}";
			}
		}
		if (leftConst && rightConst && SqlAnalyzer.isVariable()) 
			return null;
		return (not? "NOT ":"") + strLeft+" "+getStringExpression()+" "+strRight;
	}

	public abstract String getStringExpression();
	
}
