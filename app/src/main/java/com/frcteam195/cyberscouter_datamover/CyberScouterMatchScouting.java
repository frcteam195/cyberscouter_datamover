package com.frcteam195.cyberscouter_datamover;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Vector;

class CyberScouterMatchScouting {
    final static String MATCH_SCOUTING_UPDATED_FILTER = "frcteam195_cyberscoutermatchscouting_match_scouting_updated_intent_filter";

    private static boolean webQueryInProgress = false;

    private static String webResponse;

    static String getWebResponse() {
        return (webResponse);
    }

    private int matchScoutingID;
    private int eventID;
    private int matchID;
    private int matchNo;
    private int computerID;
    private int scouterID;
    private int reviewerID;
    private String team;
    private int teamMatchNo;
    private int allianceStationID;

    private int autoStartPos;
    private int autoPreload;
    private int autoDidNotShow;
    private int autoMoveBonus;
    private int autoBallLow;
    private int autoBallHigh;
    private int autoBallMiss;
    private int autoBallPos1;
    private int autoBallPos2;
    private int autoBallPos3;
    private int autoBallPos4;
    private int autoBallPos5;
    private int autoBallPos6;
    private int autoBallPos7;
    private int autoBallPos8;
    private int autoBallPos9;
    private int autoBallPos10;

    private int teleBallLow;
    private int teleBallHigh;
    private int teleBallMiss;

    private int climbStatus;
    private int rungClimbed;
    private int climbPosition;
    private int insteadOfClimb;

    private int summLaunchPad;
    private int summSortCargo;
    private int summShootDriving;
    private int summBrokeDown;
    private int summLostComm;
    private int summSubsystemBroke;
    private int summGroundPickup;
    private int summTerminalPickup;
    private int summHopperLoad;
    private int summPlayedDefense;
    private int summDefPlayedAgainst;

    private boolean matchEnded;
    private int scoutingStatus;
    private boolean complete;
    private int uploadStatus;

    public String toJSON() {
        String json = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("matchScoutingId", getMatchScoutingID());
            jsonObject.put("eventID", getEventID());
            jsonObject.put("matchID", getMatchID());
            jsonObject.put("computerID", getComplete());
            jsonObject.put("scouterID", getScouterID());

            json = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    static String getMatchesRemote(AppCompatActivity activity, SQLiteDatabase db, int eventId) {
        String ret = null;
        try {
            int last_hash = CyberScouterTimeCode.getLast_update(db);
            System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>>>LastUpdate=%d", last_hash));
            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.getMatches(activity, eventId, last_hash);
            if (null != response) {
                JSONObject jo = new JSONObject(response);
                String result = jo.getString("result");
                if (!result.equalsIgnoreCase("failure")) {
                    if (result.equalsIgnoreCase("skip")) {
                        ret = "skip";
                    } else {
                        JSONArray payload = jo.getJSONArray("payload");
                        ret = payload.toString();
                        last_hash = jo.getInt("hash");
                        CyberScouterTimeCode.setLast_update(db, last_hash);
                    }
                } else {
                    ret = "skip";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String setMatchesRemote(AppCompatActivity activity) {
        String ret = "failed";
        try {
            JSONObject jo = new JSONObject();
            jo.put("cmd", "put-match-scouting");
            jo.put("key", matchScoutingID);
            JSONObject payload = new JSONObject();
//            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SCOUTINGSTATUS, ScoutingStatus.FINISHED_SUCCESSFULLY);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOSTARTPOS, autoStartPos);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPRELOAD, autoPreload);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, autoDidNotShow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, autoMoveBonus);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLLOW, autoBallLow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLHIGH, autoBallHigh);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLMISS, autoBallMiss);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS1, autoBallPos1);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS2, autoBallPos2);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS3, autoBallPos3);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS4, autoBallPos4);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS5, autoBallPos5);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS6, autoBallPos6);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS7, autoBallPos7);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS8, autoBallPos8);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS9, autoBallPos9);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOBALLPOS10, autoBallPos10);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLLOW, teleBallLow);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLHIGH, teleBallHigh);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_TELEBALLMISS, teleBallMiss);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBSTATUS, climbStatus);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBHEIGHT, rungClimbed);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_CLIMBPOSITION, climbPosition);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLAUNCHPAD, summLaunchPad);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSORTCARGO, summSortCargo);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSHOOTDRIVING, summShootDriving);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMBROKEDOWN, summBrokeDown);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMLOSTCOMM, summLostComm);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMSUBSYSTEMBROKE, summSubsystemBroke);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMGROUNDPICKUP, summGroundPickup);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMTERMINALPICKUP, summTerminalPickup);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMPLAYEDDEFENSE, summPlayedDefense);
            payload.put(CyberScouterContract.MatchScouting.COLUMN_NAME_SUMMDEFPLAYEDAGAINST, summDefPlayedAgainst);
            jo.put("payload", payload);

            BluetoothComm btcomm = new BluetoothComm();
            String response = btcomm.sendSetCommand(activity, jo);
            if (null != response) {
                response = response.replace("x03", "");
                JSONObject jresp = new JSONObject(response);
                ret = jresp.getString("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    static void getMatchesWebService(final Activity activity, int eventId) {

        if (webQueryInProgress)
            return;

        webQueryInProgress = true;
        RequestQueue rq = Volley.newRequestQueue(activity);
//        String url = String.format("%s/match-scouting?eventId=%s", FakeBluetoothServer.webServiceBaseUrl, eventId);
        String url = "";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webQueryInProgress = false;
                        try {
                            Intent i = new Intent(MATCH_SCOUTING_UPDATED_FILTER);
                            webResponse = response;
                            i.putExtra("cyberscoutermatches", "fetch");
                            activity.sendBroadcast(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                webQueryInProgress = false;
                String msg;
                if (null == error.networkResponse) {
                    msg = error.getMessage();
                } else {
                    msg = String.format("Status Code: %d\nMessage: %s", error.networkResponse.statusCode, new String(error.networkResponse.data));
                }

                MessageBox.showMessageBox(activity, "Fetch of Match Scouting Records Failed", "CyberScouterMatchScouting.getMatchScoutingWebService",
                        String.format("Can't get list of matches to scout.\nContact a scouting mentor right away\n\n%s\n", msg));
            }
        });

        rq.add(stringRequest);
    }


    int getMatchScoutingID() {
        return matchScoutingID;
    }

    int getEventID() {
        return eventID;
    }

    int getMatchID() {
        return matchID;
    }

    int getMatchNo() {
        return matchNo;
    }

    int getComputerID() {
        return computerID;
    }

    int getScouterID() {
        return scouterID;
    }

    int getReviewerID() {
        return reviewerID;
    }

    String getTeam() {
        return team;
    }

    int getTeamMatchNo() {
        return teamMatchNo;
    }

    int getAllianceStationID() {
        return allianceStationID;
    }

    int getAutoStartPos() {return autoStartPos; }

    int getAutoPreload() {return autoPreload; }

    int getAutoDidNotShow() {
        return autoDidNotShow;
    }

    int getAutoMoveBonus() {
        return autoMoveBonus;
    }

    int getAutoBallLow() {
        return autoBallLow;
    }

    int getAutoBallHigh() {
        return autoBallHigh;
    }

    int getAutoBallMiss() {
        return autoBallMiss;
    }

    int getAutoBallPos1() {
        return autoBallPos1;
    }

    int getAutoBallPos2() {
        return autoBallPos2;
    }

    int getAutoBallPos3() {
        return autoBallPos3;
    }

    int getAutoBallPos4() {
        return autoBallPos4;
    }

    int getAutoBallPos5() {
        return autoBallPos5;
    }

    int getAutoBallPos6() {
        return autoBallPos6;
    }

    int getAutoBallPos7() {
        return autoBallPos7;
    }

    int getAutoBallPos8() {
        return autoBallPos8;
    }

    int getAutoBallPos9() {
        return autoBallPos9;
    }

    int getAutoBallPos10() {
        return autoBallPos10;
    }

    int getTeleBallLow() {
        return teleBallLow;
    }

    int getTeleBallHigh() {
        return teleBallHigh;
    }

    int getTeleBallMiss() {
        return teleBallMiss;
    }

    int getClimbStatus() {
        return rungClimbed;
    }

    int getClimbHeight() {
        return rungClimbed;
    }

    int getInsteadOfClimb() {return insteadOfClimb;}

    int getClimbPosition() {return climbPosition;}

    int getSummLaunchPad() {
        return summLaunchPad;
    }

    int getSummSortCargo() {
        return summSortCargo;
    }

    int getSummShootDriving() { return summShootDriving; }

    int getSummBrokeDown() {
        return summBrokeDown;
    }

    int getSummLostComm() {
        return summLostComm;
    }

    int getSummSubsystemBroke() {
        return summSubsystemBroke;
    }

    int getSummGroundPickup() {
        return summGroundPickup;
    }

    int getSummTerminalPickup() {
        return summTerminalPickup;
    }

    int getSummPlayedDefense() {
        return summPlayedDefense;
    }

    int getSummDefPlayedAgainst() {
        return summDefPlayedAgainst;
    }

    int getUploadStatus() {
        return uploadStatus;
    }

    boolean getMatchEnded() {
        return matchEnded;
    }

    int getScoutingStatus() {
        return scoutingStatus;
    }

    boolean getComplete() {
        return complete;
    }


    void setMatchScoutingID(int matchScoutingID) {
        this.matchScoutingID = matchScoutingID;
    }

    void setEventID(int eventID) {
        this.eventID = eventID;
    }

    void setMatchID(int matchID) {
        this.matchID = matchID;
    }

    void setComputerID(int computerID) {
        this.computerID = computerID;
    }

    void setScouterID(int scouterID) {
        this.scouterID = scouterID;
    }
}
