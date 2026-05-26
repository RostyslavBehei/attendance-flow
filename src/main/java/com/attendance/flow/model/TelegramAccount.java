package com.attendance.flow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("TELEGRAM")
@Getter
@Setter
public class TelegramAccount extends MessengerAccount {
}
