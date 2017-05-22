package com.cubeia.games.poker.admin.wicket;

import com.cubeia.network.shared.web.wicket.search.SearchEntity;
import com.cubeia.poker.handhistory.api.HistoricHand;
import com.cubeia.poker.handhistory.api.Player;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricHandSearchEntity implements SearchEntity {

    private String _id;
    private Date startTime;
    private Date endTime;
    private List<Player> seats = new ArrayList<Player>(6);


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<Player> getSeats() {
        return seats;
    }

    public void setSeats(List<Player> seats) {
        this.seats = seats;
    }
}
