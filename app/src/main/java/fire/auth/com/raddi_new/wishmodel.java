package fire.auth.com.raddi_new;

import com.google.firebase.firestore.FieldValue;

public class wishmodel {

    FieldValue time;
    String postid;

    public wishmodel(){

    }

    public wishmodel(FieldValue time, String postid) {
        this.time = time;
        this.postid = postid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public FieldValue getTime() {
        return time;
    }

    public void setTime(FieldValue time) {
        this.time = time;
    }
}
