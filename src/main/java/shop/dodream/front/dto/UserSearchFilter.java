package shop.dodream.front.dto;

import lombok.Data;

@Data
public class UserSearchFilter {
    private String userId;
    private String name;
    private String email;
    private GradeType gradeType;
    private String searchType;
    private String searchValue;

    public void setSearchValue(String searchValue) {
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            this.searchValue = searchValue.trim();

            switch (searchType) {
                case "userId":
                    this.userId = searchValue;
                    break;
                case "name":
                    this.name = searchValue;
                    break;
                case "email":
                    this.email = searchValue;
                    break;
                case "all":
                    break;
                default:
                    if (searchValue.contains("@")) {
                        this.email = searchValue;
                    } else if (searchValue.matches("^[a-zA-Z0-9_-]+$")) {
                        this.userId = searchValue;
                    } else {
                        this.name = searchValue;
                    }
                    break;
            }
        }
    }
}
