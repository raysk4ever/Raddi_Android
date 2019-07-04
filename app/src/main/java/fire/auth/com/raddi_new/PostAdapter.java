package fire.auth.com.raddi_new;

import android.widget.Spinner;

public class PostAdapter {

    String name, price, description, uid;
    long timeStamp;
    String postImage, docId;
    String userEmail, userName;
    String category, locality, subLocality,writer;
    int likes;
    String postId;

    public PostAdapter(){}

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public PostAdapter(String postId, String userEmail, String userName, String name, String price, String description, String uid, long timeStamp,
                       String postImage, String category, String writer, String locality, String subLocality, String docId, int likes) {
        this.postId = postId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.uid = uid;
        this.timeStamp = timeStamp;
        this.postImage = postImage;
        this.userName  = userName;
        this.userEmail  = userEmail;
        this.category = category;
        this.locality = locality;
        this.subLocality = subLocality;
        this.docId = docId;
        this.likes = likes;
        this.writer = writer;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }
}
