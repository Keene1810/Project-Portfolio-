﻿using System.ComponentModel.DataAnnotations;

namespace test_ngo.Models
{
    public class Volunteer
    {
        [Key]
        public int VolunteerID { get; set; }
        public string UserID { get; set; }
        public int EventID { get; set; }
        public string VolunteerName { get; set; }
        public string VolunteerSurname { get; set; }
        public string VolunteerEmail { get; set; }
        public string PhoneNumber { get; set; }
        public string Feedback { get; set; }
        public string Status { get; set; }
    }
}
