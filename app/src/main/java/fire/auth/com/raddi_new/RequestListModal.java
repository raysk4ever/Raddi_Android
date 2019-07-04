package fire.auth.com.raddi_new;

import com.google.firebase.firestore.FieldValue;

class RequestListModal {
    String status;
    String email;
    String postId;

    public RequestListModal(){}

    public RequestListModal(String status, String email, String postId) {
        this.status = status;
        this.email = email;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
