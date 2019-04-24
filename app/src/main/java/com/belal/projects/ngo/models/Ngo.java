package com.belal.projects.ngo.models;

public class Ngo {

    // make sure it matches the name of childs in database or it wont work
    private String org_name ;
    private String org_logo ;
    private String org_about ;
    private String qr_code ;
    private String cover_image ;
    private String facebook_page ;
    private String mobile_number ;
    private String email_address ;
    private String website_link ;

    // empty constructor
    public Ngo() { }

    public Ngo(String org_name, String org_logo, String org_about, String qr_code,
               String cover_image, String facebook_page, String mobile_number, String email_address, String website_link) {
        this.org_name = org_name;
        this.org_logo = org_logo;
        this.org_about = org_about;
        this.qr_code = qr_code;
        this.cover_image = cover_image;
        this.facebook_page = facebook_page;
        this.mobile_number = mobile_number;
        this.email_address = email_address;
        this.website_link = website_link;
    }


    // getters and setters
    public String getOrg_name() { return org_name; }
    public String getOrg_logo() { return org_logo; }
    public String getOrg_about() { return org_about; }
    public String getQr_code() { return qr_code; }
    public String getCover_image() { return cover_image; }
    public String getFacebook_page() { return facebook_page; }
    public String getMobile_number() { return mobile_number; }
    public String getEmail_address() { return email_address; }
    public String getWebsite_link() { return website_link; }


    public void setOrg_name(String org_name) { this.org_name = org_name; }
    public void setOrg_logo(String org_logo) { this.org_logo = org_logo; }
    public void setOrg_about(String org_about) { this.org_about = org_about; }
    public void setQr_code(String qr_code) { this.qr_code = qr_code; }
    public void setCover_image(String cover_image) { this.cover_image = cover_image; }
    public void setFacebook_page(String facebook_page) { this.facebook_page = facebook_page; }
    public void setMobile_number(String mobile_number) { this.mobile_number = mobile_number; }
    public void setEmail_address(String email_address) { this.email_address = email_address; }
    public void setWebsite_link(String website_link) { this.website_link = website_link; }

}
