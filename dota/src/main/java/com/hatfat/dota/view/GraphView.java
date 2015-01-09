package com.hatfat.dota.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.hatfat.dota.R;
import com.hatfat.dota.model.match.Match;
import com.hatfat.dota.model.match.Matches;
import com.hatfat.dota.model.player.Player;
import com.hatfat.dota.model.user.SteamUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by scottrick on 8/26/14.
 */
public class GraphView extends View {

    public static final int MAX_NUMBER_OF_MATCHES_IN_GRAPH = 50;

    private List<Integer> values;

    private Paint paint;

    private Rect bounds;
    List<PointF> points;

    public GraphView(Context context) {
        super(context);
        init(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.values = new LinkedList();

        bounds = new Rect();
        points = new LinkedList();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.graph_line_width));
    }

    public void setValues(List<Integer> values) {
        this.values = values;
        postInvalidate();
    }

    public void setValuesFromMatchListForUser(List<Long> matchIds, SteamUser user) {
        //update the graph view
        List<Integer> graphValues = new LinkedList();

        //start point
        graphValues.add(0);

        for (Long matchId : matchIds) {
            Match match = Matches.get().getMatch(matchId);
            Player player = match.getPlayerForSteamUser(user);

            Match.PlayerMatchResult matchResult = match.getPlayerMatchResultForPlayer(player);

            if (matchResult == Match.PlayerMatchResult.PLAYER_MATCH_RESULT_VICTORY) {
                graphValues.add(1);
            }
            else if (matchResult == Match.PlayerMatchResult.PLAYER_MATCH_RESULT_DEFEAT) {
                graphValues.add(-1);
            }
            else {
                graphValues.add(0);
            }
        }

        setValues(graphValues);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.getClipBounds(bounds);

        //calculate min/max values that the graph will have
        int value = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int v : values) {
            value += v;

            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        //add a little padding on the top/bot
        min -= 6;
        max += 6;

        int numStepsY = max - min;
        float yStepSize = (float) bounds.height() / (float) numStepsY;

        int numStepsX = values.size() + 1; //+1 to add some left/right padding
        float xStepSize = (float) bounds.width() / (float) numStepsX;

        //calculate the line color
        int colorThreshold = values.size() / 10;
        colorThreshold = Math.max(colorThreshold, 2); //more/less than 2 at least

        if (value > colorThreshold) {
            //positive gain!
            paint.setColor(getResources().getColor(R.color.graph_green));
        }
        else if (value < -colorThreshold) {
            //negative gain :(
            paint.setColor(getResources().getColor(R.color.graph_red));
        }
        else {
            //neutral
            paint.setColor(getResources().getColor(R.color.off_gray));
        }

        float xPos = xStepSize;
        value = 0;

        points.clear();

        for (int v : values) {
            value += v;

            int yMult = value - min;
            points.add(new PointF(xPos, bounds.height() - yMult * yStepSize));

            xPos += xStepSize;
        }

        //the path!
        Path path = new Path();

        for (PointF point : points) {
            int index = points.indexOf(point);

            if (index == 0) {
                path.moveTo(point.x, point.y);
            }
            else {
                path.lineTo(point.x, point.y);
            }
        }

        canvas.drawPath(path, paint);
    }
}
