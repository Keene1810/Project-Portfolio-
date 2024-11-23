using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using test_ngo.Data;
using test_ngo.Models;

namespace test_ngo.Controllers
{
    public class VolunteersController : Controller

    {
        private readonly ApplicationDbContext _context;
        private readonly UserManager<IdentityUser> _userManager;
        private readonly ILogger<VolunteersController> _logger;

        public VolunteersController(ApplicationDbContext context, UserManager<IdentityUser> userManager, ILogger<VolunteersController> logger)
        {
            _context = context;
            _userManager = userManager;
            _logger = logger;
        }

        // GET: Volunteers
        public async Task<IActionResult> Index()
        {
            return View(await _context.Volunteer.ToListAsync());
        }

        // GET: Volunteers/Details/5
        public async Task<IActionResult> Details(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var volunteer = await _context.Volunteer
                .FirstOrDefaultAsync(m => m.VolunteerID == id);
            if (volunteer == null)
            {
                return NotFound();
            }

            return View(volunteer);
        }

        //// GET: Volunteers/Create
        //public IActionResult Create()
        //{
        //    return View();
        //}

        //// POST: Volunteers/Create
        //// To protect from overposting attacks, enable the specific properties you want to bind to.
        //// For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.


        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public async Task<IActionResult> Create([Bind("VolunteerID,UserID,EventID,VolunteerName,VolunteerSurname,VolunteerEmail,PhoneNumber,Feedback,Status")] Volunteer volunteer)
        //{
        //    if (ModelState.IsValid)
        //    {
        //        _context.Add(volunteer);
        //        await _context.SaveChangesAsync();
        //        return RedirectToAction(nameof(Index));
        //    }
        //    return View(volunteer);
        //}


        public async Task<IActionResult> Create()
        {
            var user = await _userManager.GetUserAsync(User);
            if (user == null)
            {
                // Handle error if user is not logged in
                ModelState.AddModelError("", "User is not logged in.");
                return View();
            }

            var volunteer = new Volunteer
            {
                UserID = user.Id,
                EventID = 0, // Set EventID to a default value
                Feedback = null, // Set Feedback to null
                Status = "Pending" // Set the status to "Pending"
            };

            return View(volunteer);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create([Bind("VolunteerID,UserID,EventID,VolunteerName,VolunteerSurname,VolunteerEmail,PhoneNumber,Feedback,Status")] Volunteer volunteer)
        {
            if (ModelState.IsValid)
            {
                _context.Add(volunteer);
                await _context.SaveChangesAsync();
                return RedirectToAction("AwaitApproval", new {id = volunteer.VolunteerID});
            }
            return View(volunteer);
        }

        public IActionResult AwaitApproval(int id)
        {
            // Retrieve volunteer information from database if needed
            var volunteer = _context.Volunteer.FirstOrDefault(v => v.VolunteerID == id);
            if (volunteer == null)
            {
                // Handle case where volunteer with specified ID is not found
                return RedirectToAction("Index", "Home"); // Redirect to home page or handle as appropriate
            }

            return View(volunteer); // Return the Success view with the volunteer model
        }



        // GET: Volunteers/Edit/5
        public async Task<IActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var volunteer = await _context.Volunteer.FindAsync(id);
            if (volunteer == null)
            {
                return NotFound();
            }
            return View(volunteer);
        }

        // POST: Volunteers/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, [Bind("VolunteerID,UserID,EventID,VolunteerName,VolunteerSurname,VolunteerEmail,PhoneNumber,Feedback,Status")] Volunteer volunteer)
        {
            if (id != volunteer.VolunteerID)
            {
                return NotFound();
            }

            if (ModelState.IsValid)
            {
                try
                {
                    _context.Update(volunteer);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!VolunteerExists(volunteer.VolunteerID))
                    {
                        return NotFound();
                    }
                    else
                    {
                        throw;
                    }
                }
                return RedirectToAction(nameof(Index));
            }
            return View(volunteer);
        }

        // GET: Volunteers/Delete/5
        public async Task<IActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var volunteer = await _context.Volunteer
                .FirstOrDefaultAsync(m => m.VolunteerID == id);
            if (volunteer == null)
            {
                return NotFound();
            }

            return View(volunteer);
        }

        // POST: Volunteers/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            var volunteer = await _context.Volunteer.FindAsync(id);
            if (volunteer != null)
            {
                _context.Volunteer.Remove(volunteer);
            }

            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool VolunteerExists(int id)
        {
            return _context.Volunteer.Any(e => e.VolunteerID == id);
        }

        public IActionResult Application()
        {
            return View();
        }

        public IActionResult Volunteer()
        {
            return View();
        }
        public IActionResult VolunteerApplications()
        {
            var pendingVolunteers = _context.Volunteer.Where(v => v.Status == "Pending").ToList();
            return View(pendingVolunteers);
        }

        [HttpPost]
        public async Task<IActionResult> Approve(int id)
        {
            var volunteer = await _context.Volunteer.FindAsync(id);
            if (volunteer == null)
            {
                return NotFound();
            }

            volunteer.Status = "Approved";
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(VolunteerApplications));
        }

        [HttpPost]
        public async Task<IActionResult> Decline(int id)
        {
            var volunteer = await _context.Volunteer.FindAsync(id);
            if (volunteer == null)
            {
                return NotFound();
            }

            volunteer.Status = "Declined";
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(VolunteerApplications));
        }
        public IActionResult Apply()
        {
            // Check if user is authenticated
            ViewData["ShowLoginPopup"] = !User.Identity.IsAuthenticated;

            return View();
        }

    }
}
