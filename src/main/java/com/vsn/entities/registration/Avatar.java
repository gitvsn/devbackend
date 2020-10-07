package com.vsn.entities.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsn.entities.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_avatars")
public class Avatar  extends BaseEntity {

  @JsonIgnore
  @Column(name = "user_id")
  private long userId;

  @Lob
  private byte[] avatar;

}